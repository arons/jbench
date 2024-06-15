package ch.arons.jbench.pg.test;

import java.sql.Connection;
import java.sql.SQLException;

import ch.arons.jbench.pg.DB;

/**
 * Latency of opening a new connection.
 */
public class OpenLatency extends DBTest {

    public OpenLatency(DB db) {
        super(db);
    }
    
    @Override
    public void test() {
        int numberTest = 10;
        
        int count = 0;
        long totConnMs = 0;
        long maxConnMs = 0;
        long minConnMs = Long.MAX_VALUE;
        for (int i = 0; i < numberTest; i++) {
            long currentMs = testConnectio();
            if (currentMs > 0) {
                count++;
                totConnMs += currentMs;
                if (maxConnMs < currentMs) {
                    maxConnMs = currentMs;
                }
                if (minConnMs > currentMs) {
                    minConnMs = currentMs;
                }
            }
        }
        
        System.out.printf("Connection open latency:\n");
        System.out.printf(" avg: %d ms \n", totConnMs / (count));
        System.out.printf(" max: %d ms \n", maxConnMs);
        System.out.printf(" min: %d ms \n", minConnMs);
    }

    private long testConnectio() {
        Connection c = null;
        try {
            long start = System.currentTimeMillis();
            c = getConnection();
            long duration = System.currentTimeMillis() - start;
            return duration;
        } catch (SQLException e) {
            System.err.println("could not connect to DB: " + e.getMessage());
            return -1;
        } finally {
            if (c != null) {
                try { 
                    c.close(); 
                } catch (SQLException e) {
                    //nothing to do
                }
            }
        }
    }
}
