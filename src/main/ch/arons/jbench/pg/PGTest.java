package ch.arons.jbench.pg;

import java.sql.DriverManager;
import java.sql.SQLException;

import org.postgresql.Driver;

import ch.arons.jbench.pg.data.DataClean;
import ch.arons.jbench.pg.data.DataCreate;
import ch.arons.jbench.pg.test.PGParameterCheck;

/**
 * Perform pg tests.
 */
public class PGTest {

    
    private DB db;
    public String additional;

    public PGTest(String url, String user, String pw) throws SQLException {
        DriverManager.registerDriver(new Driver());
        this.db = new DB(url, user, pw);
    }
    
    public void setAdditional(String additional) {
        this.additional = additional;
    }

    public void params() {
        PGParameterCheck t = new PGParameterCheck(db);
        t.checks();
    }

    public void prepare() throws SQLException {
        (new DataCreate(db)).run();
    }

    public void clean() throws SQLException {
        (new DataClean(db)).run();
    }

    public void run() {
        // TODO Auto-generated method stub
        
    }


}
