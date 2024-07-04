package ch.arons.jbench.pg.test.parallel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.TimeZone;

import ch.arons.jbench.pg.DB;

/**
 * Run single client operation.
 * 
 * <p>
 * It start a transaction and executes in loops:
 *  - commit every commitOperations
 *  - select from big table by id index
 *  - update text in big table selecting by id index
 *  - insert into big table new record
 * </p>
 */
public class SingleClient implements Runnable {

    private Random random = new Random();
    
    DB db;
    int commitOperations;
    int minId;
    int maxId;
    boolean requestInterrupt;

    public long operations; // approximations
    public long statements;
    public long skipped;
    public long durationSelect;
    public long durationUpdate;
    public long durationInsert;
    public long durationCommit;
    public long durationRuntime;

    /**
     * Configuration. 
     * 
     * @param db to connect
     * @param minId big table min id
     * @param maxId big table max id
     * @param numberOperations max number of operation
     * @param commitOperations commit every
     */
    public SingleClient(DB db, int minId, int maxId, int commitOperations) {
        this.db = db;
        
        this.minId = minId;
        this.maxId = maxId;
        
        this.commitOperations = commitOperations;
    }

    public void interrupt() {
        requestInterrupt = true;
    }

    @Override
    public void run() {
        long startExtMs = System.currentTimeMillis();

        Random random = new Random();
        
        GregorianCalendar cal = new GregorianCalendar(TimeZone.getTimeZone("Europe/Zurich"));
        try {
            cal.setTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("1974-01-01T00:00:00"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        

        try (Connection c = db.getConnection()) {
            c.setAutoCommit(false);

            while (true) {
                
                operations++;
                
                if (requestInterrupt || (operations % commitOperations == 0)) {
                    long startMs = System.currentTimeMillis();
                    c.commit();
                    long durationMs = System.currentTimeMillis() - startMs;
                    durationCommit += durationMs;

                    if (requestInterrupt) {
                        break;
                    }
                }

                int parentId = -1;
                int randomChild = minId + random.nextInt(maxId - minId);
                try (PreparedStatement ps = c.prepareStatement("select * from jbench.tbbm_child where id = ?")) {
                    ps.setFetchSize(1024);
                    ps.setInt(1, randomChild);
                    long startMs = System.currentTimeMillis();
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        parentId = rs.getInt("parent_id");
                    }
                    rs.close();
                    long durationMs = System.currentTimeMillis() - startMs;
                    statements++;
                    durationSelect += durationMs;
                } catch (SQLException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }

                if (parentId < 0) {
                    skipped++;
                    continue;
                }

                try (PreparedStatement ps = c.prepareStatement("update jbench.tbbm_child set data4 = ? where id = ? ")) {
                    ps.setFetchSize(1024);
                    ps.setString(1, getRandomString(10));
                    ps.setInt(2, randomChild);
                    long startMs = System.currentTimeMillis();
                    ps.executeUpdate();
                    long durationMs = System.currentTimeMillis() - startMs;
                    statements++;
                    durationUpdate += durationMs;
                } catch (SQLException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }

                try (PreparedStatement pchild = c.prepareStatement( 
                     " insert into jbench.tbbm_child(id, parent_id, data1, data2, data3, data4) values (nextval('jbench.tbbm_child_seq'), ?, ?, ?, ?, ?) ")) {
                    

                    Timestamp fixTimestamp = new Timestamp(cal.getTimeInMillis());
                    cal.add(Calendar.DAY_OF_MONTH, 1);

                    pchild.setFetchSize(1024);
                    pchild.setInt(1, parentId);
                    pchild.setInt(2, 1974);
                    pchild.setInt(3, 1979);
                    pchild.setTimestamp(4, fixTimestamp);
                    pchild.setString(5, getRandomString(10));

                    long startMs = System.currentTimeMillis();
                    pchild.executeUpdate();
                    long durationMs = System.currentTimeMillis() - startMs;
                    statements++;
                    durationInsert += durationMs;
                } catch (SQLException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }

                
            }
        } catch (SQLException e) {
            //nothing to do
        }

        durationRuntime = System.currentTimeMillis() - startExtMs;
    }
    
    

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
