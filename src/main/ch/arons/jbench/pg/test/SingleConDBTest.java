package ch.arons.jbench.pg.test;

import java.sql.Connection;
import java.sql.SQLException;

import ch.arons.jbench.pg.DB;

/**
 * Base class for test that use only a single db connection.
 * Takes care of opening and closing the connection.
 */
public abstract class SingleConDBTest extends DBTest {

    public SingleConDBTest(DB db) {
        super(db);
    }

    @Override
    public final void test() {
        Connection c = null;
        try {
            c = getConnection();
            test(c);
        } catch (SQLException e) {
            e.printStackTrace(); 
            return;
        } finally {
            if (c != null) try { c.close(); } catch (SQLException e) {}
        }        
    }

    /**
     * Implementation can do SQL with the connection.
     * @param c a fresh db connection
     * @throws SQLException
     */
    protected abstract void test(Connection c) throws SQLException;
    
}
