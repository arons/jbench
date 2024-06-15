package ch.arons.jbench.pg.test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.management.timer.Timer;

import ch.arons.jbench.pg.DB;

/**
 * QPS of trivial query: select 1 from dual.
 * Gives the min query latency.
 */
public class TrivialQuery extends SingleConDBTest {

    
    private static final int runtTimeMs = 3000;
    private static final int minimalQueries = 1000;
    
    public TrivialQuery(DB db) {
        super(db);
    }

    @Override
    protected void test(Connection c) throws SQLException {
        
        
        try (PreparedStatement ps = c.prepareStatement(" select 1 ");) {
            ResultSet rs = null;
            
            System.out.printf("Trivial SELECT test with fetch: ");
            long totStart = System.currentTimeMillis();
            int count = 0;
            while (true) {
                if (rs != null) {
                    rs.close();
                }
                rs = ps.executeQuery();
                if (rs.next()) {
                    rs.getInt(1);
                }
                if (count++ % minimalQueries == 0) {
                    long duration = System.currentTimeMillis() - totStart;
                    if (duration > runtTimeMs) {
                        break;
                    }
                }
            }
            long totDuration = System.currentTimeMillis() - totStart;
            
            
            
            float qps = (float) count / ((float) totDuration / Timer.ONE_SECOND);
            System.out.printf("%f qps\n", qps);
            float lat = (float) totDuration / count;
            System.out.printf("=> min. query latency: %f ms (should be dominated only by network latency. Queries: %d)\n", lat, count);
            
            
            
            System.out.printf("Trivial SELECT test NO fetch: ");
            totStart = System.currentTimeMillis();
            count = 0;
            while (true) {
                if (rs != null) {
                    rs.close();
                }
                rs = ps.executeQuery();
                if (count++ % minimalQueries == 0) {
                    long duration = System.currentTimeMillis() - totStart;
                    if (duration > runtTimeMs) {
                        break;
                    }
                }
            }
            totDuration = System.currentTimeMillis() - totStart;
            
            
            qps = (float) count / ((float) totDuration / Timer.ONE_SECOND);
            System.out.printf("%f qps\n", qps);
            lat = (float) totDuration / count;
            System.out.printf("=> min. query latency: %f ms (should be dominated only by network latency. Queries: %d)\n", lat, count);
            
            
        } 
    }

}
