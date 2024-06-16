package ch.arons.jbench.pg.test;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Random;

import ch.arons.jbench.pg.DB;

/**
 * Test usage of array for select.
 * 
 * <p>
 * Ref: https://www.postgresql.org/docs/15/functions-array.html
 * example: select * from unnest(ARRAY[1,2], ARRAY['foo','bar','baz']) as x(a,b) 
 * 
 * Should show the difference in performance against usage of temporary table.
 * </p>
 */
public class ArrayQuery extends SingleConDBTest {

    public ArrayQuery(DB db) {
        super(db);
    }

    
    private void testArrayTableSort(Connection c, int size) {
        System.out.printf("Table Arraysize %d. ", size);
        
        try {
            boolean autocommit = c.getAutoCommit();
            c.setAutoCommit(false);
            // generate 100 random number between 0 to 100 
            Random random = new Random();
            String[] s1 = new String[size];
            Arrays.fill(s1, "param_name");
            Long[]  l1 = new Long[size];
            Arrays.setAll(l1, i -> Long.valueOf(random.nextLong(size)));
            
            
            
            try (PreparedStatement ps = c.prepareStatement("select * from unnest(?,?) nTable(a_name,a_value) order by nTable.a_name, nTable.a_value ");) {
                ps.setFetchSize(1024);
                long exeStartMs = System.currentTimeMillis();
                
                Array strArray = c.createArrayOf("text", s1);
                ps.setArray(1, strArray);
                
                Array longArray = c.createArrayOf("bigint", l1);
                ps.setArray(2, longArray);
                
                
                ResultSet rs = ps.executeQuery();
                long exeEndms = System.currentTimeMillis();
                rs.close();
                System.out.printf("Sort: %d ms. ", (exeEndms - exeStartMs) );
            } catch (SQLException e) {
                throw e;
            }
            
            
            long commitStartMs = System.currentTimeMillis();
            c.commit();
            long commitEndms = System.currentTimeMillis();
            System.out.printf("Commit: %d ms\n",  (commitEndms - commitStartMs));
            
            c.setAutoCommit(autocommit);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void test(Connection c) throws SQLException {
        testArrayTableSort(c, 1000);
        testArrayTableSort(c, 100000);
        testArrayTableSort(c, 1000000);
    }
}
