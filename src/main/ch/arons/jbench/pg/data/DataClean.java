package ch.arons.jbench.pg.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import ch.arons.jbench.pg.DB;

/**
 * Clean data created.
 */
public class DataClean {
    private DB db;
    
    public DataClean(DB db) throws SQLException {
        this.db = db;
    }
    
    /**
     * Main method.
     */
    public void run() {
        System.out.println("DataClean start " + (new Date()));
        try (Connection conn = db.getConnection()) {
            
            System.out.printf("Cleaning data...\n"); 
            
            
            try (PreparedStatement c = conn.prepareStatement("drop schema if exists jbench cascade")) {
                c.executeUpdate();
            } catch (SQLException e) {
                System.err.println(e.getMessage() + ": tbbm_lob");
            }
                    
            System.out.printf("Cleaning data done\n");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        System.out.println("DataClean end " + (new Date()));
    }
}
