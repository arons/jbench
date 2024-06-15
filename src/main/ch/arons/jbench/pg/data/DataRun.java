package ch.arons.jbench.pg.data;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Execute a query.
 */
public class DataRun {
    
    /**
     * Execute query.
     * 
     * @param conn connection
     * @param sql query to execute
     */
    public static void executeQuery( Connection conn, String sql) {
        try ( PreparedStatement c = conn.prepareStatement(sql) ) { 
            c.executeUpdate(); 
        } catch (Exception e) { 
            e.printStackTrace();
            System.err.println(e.getMessage()); 
        }
    }
}
