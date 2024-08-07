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
import java.util.concurrent.atomic.AtomicBoolean;

import ch.arons.jbench.pg.DB;

/**
 * Run single client operation.
 * Is tpcb-like client, it perform 1 transaction with 3 select 1 update and 1 insert.
 * <p>
 *  - select from big table by id index (3 times)
 *  - update text in big table selecting by id index (1 time)
 *  - insert into big table new record ( 1 time)
 *  - commit transaction
 * </p>
 */
public class SingleClient implements Runnable {

    private Random random = new Random();
    
    DB db;
    int minId;
    int maxId;
    AtomicBoolean requestInterrupt;

    public long transaction; // approximations
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
     */
    public SingleClient(DB db, int minId, int maxId) {
        this.db = db;
        this.minId = minId;
        this.maxId = maxId;
        this.requestInterrupt = new AtomicBoolean(false);
    }

    public void interrupt() {
        requestInterrupt.set(true);
    }

    @Override
    public void run() {
        Random random = new Random();
        GregorianCalendar cal = new GregorianCalendar(TimeZone.getTimeZone("Europe/Zurich"));
        try {
            cal.setTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("1974-01-01T00:00:00"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        
        long startExtMs = System.currentTimeMillis();
        try (Connection c = db.getConnection()) {
            c.setAutoCommit(false);
            startExtMs = System.currentTimeMillis();
            
            while (true) {
                
                if (requestInterrupt.get()) {
                    break;
                }
                
                transaction++;
                

                int parentId = -1;
                
                int randomChild = minId + random.nextInt(maxId - minId);
                
                for ( int i = 1; i < 3; i++ ) {
                    randomChild = minId + random.nextInt(maxId - minId);
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

                
                
                long startMs = System.currentTimeMillis();
                c.commit();
                long durationMs = System.currentTimeMillis() - startMs;
                durationCommit += durationMs;
                
                
                
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
