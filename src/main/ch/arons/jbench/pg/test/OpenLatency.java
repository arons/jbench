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
        Connection c = null;
        try {
            long start = System.currentTimeMillis();
            c = getConnection();
            long duration = System.currentTimeMillis() - start;
            System.out.printf("Connection open latency: %d ms \n", duration);
        } catch (SQLException e) {
            System.err.println("could not connect to DB: " + e.getMessage());
            return;
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
