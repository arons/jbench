package ch.arons.jbench.pg.test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import ch.arons.jbench.pg.DB;

/**
 * Print most common parameter to be checked.
 */
public class PGParameterCheck {
    private final DB db;
    
    public PGParameterCheck(DB db) {
        this.db = db;
    }
    
    /**
     * perform the check.
     */
    public void checks() {
        System.out.println("ParameterCheck start " + (new Date()));
        try (Connection conn = db.getConnection()) {
            pgversion(conn);
            pgparameters(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            System.out.println("ParameterCheck end " + (new Date()));
        }
    }


    private void pgversion(Connection conn) {
        try (PreparedStatement ps = conn.prepareStatement(" SELECT version() ")) {
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String version = rs.getString(1);
                System.out.printf("Version: %s\n", version);
            } else {
                System.err.printf("Error getting pg version\n");
            }
            rs.close();
            
        } catch (SQLException e) { 
            e.printStackTrace();
            System.err.printf("Error getting pg version: %s\n", e.getMessage()); 
        }
    }

    private void pgparameters(Connection conn) {

        System.out.printf("Parameters:\n");
        try (PreparedStatement ps = conn.prepareStatement("""
select 
name, 
setting,
short_desc,
context,
source
from pg_settings
where name in ( 
'max_connections',
'shared_buffers',
'effective_cache_size',
'checkpoint_completion_target',
'work_mem',
'temp_buffers',
'maintenance_work_mem',
'default_toast_compression',
'checkpoint_timeout',
'random_page_cost',
'max_worker_processes',
'max_parallel_workers',
'max_parallel_workers_per_gather',
'max_locks_per_transaction',
'compute_query_id'
'track_activities'
)
order by name
                """)) {
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String name = rs.getString("name");
                String setting = rs.getString("setting");
                String shortDesc = rs.getString("short_desc");
                
                        
                System.out.printf("%40s | %10s | %s\n", name, setting, shortDesc);
            }
            rs.close();
            
        } catch (SQLException e) { 
            e.printStackTrace();
            System.err.printf("Error getting pg parameters: %s\n", e.getMessage()); 
        }
    }


}

