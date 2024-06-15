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

    public TrivialQuery(DB db) {
        super(db);
    }

    @Override
    protected void test(Connection c) throws SQLException {
        
        
        try (PreparedStatement ps = c.prepareStatement(" select 1 ");) {
            ResultSet rs = null;
            
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
                if (count++ % 1000 == 0) {
                    long duration = System.currentTimeMillis() - totStart;
                    if (duration > 3000) {
                        break;
                    }
                }
            }
            long totDuration = System.currentTimeMillis() - totStart;
            
            
            System.out.printf("Trivial SELECT test with fetch");
            float qps = (float) count / ((float) totDuration / Timer.ONE_SECOND);
            System.out.printf("Trivial SELECT: %f qps\n", qps);
            float lat = (float) totDuration / count;
            System.out.printf("=> min. query latency: %f ms (should be dominated only by network latency)\n", lat);
            
            
            
            totStart = System.currentTimeMillis();
            count = 0;
            while (true) {
                if (rs != null) {
                    rs.close();
                }
                rs = ps.executeQuery();
                if (count++ % 1000 == 0) {
                    long duration = System.currentTimeMillis() - totStart;
                    if (duration > 3000) {
                        break;
                    }
                }
            }
            totDuration = System.currentTimeMillis() - totStart;
            
            System.out.printf("Trivial SELECT test NO fetch");
            qps = (float) count / ((float) totDuration / Timer.ONE_SECOND);
            System.out.printf("Trivial SELECT: %f qps\n", qps);
            lat = (float) totDuration / count;
            System.out.printf("=> min. query latency: %f ms (should be dominated only by network latency)\n", lat);
            
            
        } 
    }

}
