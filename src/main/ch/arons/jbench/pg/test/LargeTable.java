package ch.arons.jbench.pg.test;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import ch.arons.jbench.pg.DB;

/**
 * Perform test over tbbm_child which contains more than 1M rows.
 * Fetch first 256 rows.
 * Get Time to first row (TTFR) and time to last row (TTLR.
 * Where filter on indexed cols by date.
 * 
 */
public class LargeTable extends SingleConDBTest {

    public LargeTable(DB db) {
        super(db);
    }
    
    @Override
    protected void test(Connection c) throws SQLException {
        System.out.printf("Select from larg table:\n");
        
        try (PreparedStatement ps = c.prepareStatement("select * from jbench.tbbm_child where data3 between ? and ? fetch first 1000 rows only");) {
            
            long start = System.currentTimeMillis();
            ps.setDate(1, new Date(1980 - 1900, 1 - 1, 1));
            ps.setDate(2, new Date(1980 - 1900, 12 - 1, 31));
            ps.setFetchSize(256);
            ResultSet rs = ps.executeQuery();
            long duration = System.currentTimeMillis() - start;
            
            System.out.printf("Time to first row LARGE tab, indexed: %d ms\n", duration);
            
            
            int count = 0;
            while (rs.next()) {
                // data4 is a random string of size 10
                String s = rs.getString("data4");
                count++;
            }
            duration = System.currentTimeMillis() - start;
            System.out.printf("Time to last row LARGE tab, indexed: %d ms\n", duration);
            
            System.out.printf("  Rows: %d\n", count);
        } 
    }
}
