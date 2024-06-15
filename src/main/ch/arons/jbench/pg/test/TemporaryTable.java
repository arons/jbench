package ch.arons.jbench.pg.test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.stream.LongStream;

import ch.arons.jbench.pg.DB;

/**
 * Test temporary table.
 */
public class TemporaryTable extends SingleConDBTest {

    public TemporaryTable(DB db) {
        super(db);
    }

    private static final String SQL_CREATES = """
CREATE TEMPORARY TABLE IF NOT EXISTS tbtest_tmp_intarray(
    t_name text not null,
    t_value bigint not null
)on commit drop;  
            """;
    
    
    private static void startSession(Connection c) {
        //System.out.printf("Create Temporary table\n");
        try (PreparedStatement s = c.prepareStatement(SQL_CREATES)) {
            s.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    
    private static void insertData(Connection conn, String name, long[] values) throws SQLException {
        if (conn == null) {
            throw new IllegalArgumentException("No open connection");
        }
        if (values == null || values.length <= 0) {
            throw new IllegalArgumentException("No values");
        }
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("insert into tbtest_tmp_intarray values( ?, ? )");
            ps.setString(1, name);
            
            for (long pid : values) {
                ps.setLong(2, pid);
                ps.addBatch();
            }
            ps.executeBatch();
            ps.close();
            ps = null;
            
        } catch (SQLException e) {
            throw e;
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) { 
                    /* nothing we can do */ 
                }
            }
        }
    }
    
    
    private void testTemporaryTableInsertAndSort(Connection c, int size) {
        System.out.printf("TemporaryTable insert size %d. ", size);
        
        try {
            c.setAutoCommit(false);
            startSession(c);
            
            // generate 100 random number between 0 to 100 
            long[]  v1 = LongStream.generate(() -> new Random().nextLong(size)).limit(size).toArray();
            
            long insertStartMs = System.currentTimeMillis();
            insertData(c, "param_name", v1);
            long insertEndms = System.currentTimeMillis();
            System.out.printf("Insert: %d ms. ", (insertEndms - insertStartMs) );
            
            try (PreparedStatement ps = c.prepareStatement("select * from tbtest_tmp_intarray order by t_name, t_value");) {
                ps.setFetchSize(1024);
                long exeStartMs = System.currentTimeMillis();
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
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                c.commit();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                c.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
    }

    @Override
    protected void test(Connection c) throws SQLException {
        testTemporaryTableInsertAndSort(c, 1000);
        testTemporaryTableInsertAndSort(c, 100000);
        testTemporaryTableInsertAndSort(c, 1000000);
    }
}
