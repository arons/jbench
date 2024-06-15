package ch.arons.jbench.pg.test;

import java.sql.Connection;
import java.sql.SQLException;

import ch.arons.jbench.pg.DB;

/**
 * Suer class for tests.
 */
public abstract class DBTest {
    protected final DB db;

    public DBTest(DB db) {
        this.db = db;
    }
    
    protected Connection getConnection() throws SQLException {
        return db.getConnection();
    }
    
    public abstract void test();
}
