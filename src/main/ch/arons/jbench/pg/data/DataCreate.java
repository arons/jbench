package ch.arons.jbench.pg.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.TimeZone;

import ch.arons.jbench.pg.DB;

/**
 * Create data for test.
 * 
 * <p>
 * Tables:
 * 
 * tbbm_lob
 * tbbm_parent = p_number rows
 * tbbm_child  = c_number * p_number rows
 * 
 * tbbm_child is 1M row.
 * 
 * </p>
 */
public class DataCreate {
    
    private Random random = new Random();
    private DB db;
    
    public DataCreate(DB db) throws SQLException {
        this.db = db;
    }
    
    private static final String SQL_STR = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. "
            + "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate "
            + "velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
    public static int p_number = 10000;
    public static int c_number = 100;
    
    
    /**
     * Main method.
     */
    public void run() {
        System.out.println("DataCreate start " + (new Date()));
        
        try (Connection conn = db.getConnection()) {
            
            DataRun.executeQuery(conn, " CREATE SCHEMA jbench ");
            
            System.out.printf("Create table jbench.tbbm_lob\n");    
            DataRun.executeQuery(conn, """
create table jbench.tbbm_lob (
  id text     not null,
  doc bytea        null,
  constraint pk_bmlob primary key(id)
)
                    """);
            
            //mid-size (10k rows), long rows
            System.out.printf("Create table jbench.tbbm_parent\n");
            DataRun.executeQuery(conn, """
create table jbench.tbbm_parent (
  id bigint not null,
  data1 text null,
  data2 text null,
  data3 text null,
  constraint pk_bmprt primary key (id)
) with (fillfactor = 85)                    
                    """);
            
            
            // large (1M rows), short rows
            System.out.printf("Create table jbench.tbbm_child\n");
            DataRun.executeQuery(conn, """
create table jbench.tbbm_child (
  id bigint not null,
  parent_id bigint not null,
  data1 bigint null,
  data2 bigint null,
  data3 timestamptz null,
  data4 text null,
  constraint pk_bmchld primary key (id),
  constraint fk_bmchldprt foreign key (parent_id) references jbench.tbbm_parent (id)
) with (fillfactor = 85)                    
                    """);
            
            
            DataRun.executeQuery( conn, "create sequence jbench.tbbm_child_seq cache 1000");
            
            /*
             * Create indexes
             */
            System.out.printf("Create indexes\n");
            DataRun.executeQuery( conn, "create index i_fk_bmchldprt on jbench.tbbm_child(parent_id, id)");
            DataRun.executeQuery( conn, "create index i_bmchld_d3 on jbench.tbbm_child(data3)");
            
            String sqlInsertParent = " insert into jbench.tbbm_parent (id, data1, data2, data3) values (?, ?, ?, ?)";
            String sqlInsertChild = " insert into jbench.tbbm_child(id, parent_id, data1, data2, data3, data4) values (nextval('jbench.tbbm_child_seq'), ?, ?, ?, ?, ?) ";
            
            /*
             * Create data
             */
            System.out.printf("Creating data...\n");
            long createStartMs = System.currentTimeMillis();
            
            
            try (PreparedStatement pparent = conn.prepareStatement(sqlInsertParent); 
                 PreparedStatement pchild = conn.prepareStatement(sqlInsertChild)) {
                
                
                GregorianCalendar cal = new GregorianCalendar(TimeZone.getTimeZone("Europe/Zurich"));
                try {
                    cal.setTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("1974-01-01T00:00:00"));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                
                
                pparent.setString(2, SQL_STR);
                pparent.setString(3, SQL_STR);
                pparent.setString(4, SQL_STR);
                
                for (int i = 0; i < p_number; i++) {
                    pparent.setLong(1, i);
                    pparent.addBatch();
                    
                    
                    Timestamp fixTimestamp = new Timestamp(cal.getTimeInMillis());
                    cal.add(Calendar.DAY_OF_MONTH, 1);
                    
                    for (int j = 0; j < c_number; j++) {
                        //pchild.setInt(1, i * c_number + j);
                        pchild.setLong(1, i);
                        pchild.setInt(2, 1974);
                        pchild.setInt(3, 1979);
                        pchild.setTimestamp(4, fixTimestamp);
                        pchild.setString(5, getRandomString(10));
                        pchild.addBatch();
                    }
                }
                
                int[] results = pparent.executeBatch();
                //check results
                for (int k = 0; k < results.length; k++) {
                    if (results[k] == Statement.EXECUTE_FAILED) {
                        System.err.println("Data preparation fail! Parents");
                    }
                }
                
                results = pchild.executeBatch();
                //check results
                for (int k = 0; k < results.length; k++) {
                    if (results[k] == Statement.EXECUTE_FAILED) {
                        System.err.println("Data preparation fail! Child");
                    }
                }
            }
            System.out.printf("Creating data done ms: %d\n", (System.currentTimeMillis() - createStartMs));
            
            
            
            System.out.printf("Perform statistics\n");
            DataRun.executeQuery(conn, "analyze jbench.TBBM_PARENT");
            DataRun.executeQuery(conn, "analyze jbench.TBBM_CHILD");
            
            
            /*
             * Perform some check on data created
             */
            try (PreparedStatement ps = conn.prepareStatement("select count(*) from  jbench.tbbm_parent")) {
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    System.out.printf("Created %d rows into jbench.tbbm_parent\n", rs.getInt(1));
                }    
            }
            
            try (PreparedStatement ps = conn.prepareStatement("select count(*) from jbench.tbbm_child")) {
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    System.out.printf("Created %d rows into jbench.tbbm_child\n", rs.getInt(1));
                }    
            }
            
            System.out.printf("You can now start benchmark\n");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        System.out.println("DataCreate end " + (new Date()));
    }
    
    

    /**
     * Creates a random string.
     * @param size size of string to be generated
     * @return a random string of size
     */
    private String getRandomString(int size) {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        StringBuilder buffer = new StringBuilder(size);
        for (int i = 0; i < size; i++) {
            int randomLimitedInt = leftLimit + (int) (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString();
    }

}
