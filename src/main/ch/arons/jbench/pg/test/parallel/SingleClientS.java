package ch.arons.jbench.pg.test.parallel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicLong;

import ch.arons.jbench.pg.DB;

public class SingleClientS implements Runnable {

    DB db;
    int commitOperations = 1000; 
    int minId;
    int maxId;
    long createdMs;
    AtomicLong operationCount;
    Random random = new Random();
    GregorianCalendar cal = new GregorianCalendar(TimeZone.getTimeZone("Europe/Zurich"));
    long operations; // approximations
    
    /**
     * Configuration. 
     * 
     * @param db to connect
     * @param minId big table min id
     * @param maxId big table max id
     * @param numberOperations max number of operation
     * @param commitOperations commit every
     */
    public SingleClientS(DB db, int minId, int maxId, AtomicLong operationCount) {
        this.db = db;
        this.minId = minId;
        this.maxId = maxId;
        this.operationCount = operationCount;
    }
    
    @Override
    public void run() {
        
        
        try (Connection c = db.getConnection()) {
            this.createdMs = System.currentTimeMillis();
            c.setAutoCommit(false);
            
            while (System.currentTimeMillis() < createdMs + 10000L) {
                singleTransaction(c);
            }
            
        } catch (SQLException e) {
            //nothing to do
            e.printStackTrace();
        }
    }
    
    /**
     * in a single transactiona tipical instance is 80% read 20% write
     * @throws SQLException 
     */
    private void singleTransaction(Connection c ) throws SQLException {
        
        
        
        int parentId = -1;
        
            int randomChild = minId + random.nextInt(maxId - minId);
            
//            try (PreparedStatement ps = c.prepareStatement("select * from jbench.tbbm_child where id = ?")) {
//                ps.setFetchSize(1024);
//                ps.setInt(1, randomChild);
//                long startMs = System.currentTimeMillis();
//                ResultSet rs = ps.executeQuery();
//                while (rs.next()) {
//                    parentId = rs.getInt("parent_id");
//                }
//                rs.close();
//                long durationMs = System.currentTimeMillis() - startMs;
//                operationCount.incrementAndGet();
//            } catch (SQLException e) {
//                e.printStackTrace();
//                throw new RuntimeException(e);
//            }
        
            try (PreparedStatement ps = c.prepareStatement("update jbench.tbbm_child set data4 = ? where id = ? ")) {
                ps.setFetchSize(1024);
                ps.setString(1, getRandomString(10));
                ps.setInt(2, randomChild);
                long startMs = System.currentTimeMillis();
                ps.executeUpdate();
                long durationMs = System.currentTimeMillis() - startMs;
                operationCount.incrementAndGet();
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        
        
//            try (PreparedStatement pchild = c.prepareStatement( 
//                    " insert into jbench.tbbm_child(id, parent_id, data1, data2, data3, data4) values (nextval('jbench.tbbm_child_seq'), ?, ?, ?, ?, ?) ")) {
//                   
//
//                   Timestamp fixTimestamp = new Timestamp(cal.getTimeInMillis());
//                   cal.add(Calendar.DAY_OF_MONTH, 1);
//
//                   pchild.setFetchSize(1024);
//                   pchild.setInt(1, parentId);
//                   pchild.setInt(2, 1974);
//                   pchild.setInt(3, 1979);
//                   pchild.setTimestamp(4, fixTimestamp);
//                   pchild.setString(5, getRandomString(10));
//
//                   long startMs = System.currentTimeMillis();
//                   pchild.executeUpdate();
//                   long durationMs = System.currentTimeMillis() - startMs;
//                   operationCount.incrementAndGet();
//               } catch (SQLException e) {
//                   e.printStackTrace();
//                   throw new RuntimeException(e);
//               }
        
        c.commit();
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
