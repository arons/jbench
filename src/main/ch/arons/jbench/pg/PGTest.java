package ch.arons.jbench.pg;

import java.sql.DriverManager;
import java.sql.SQLException;

import org.postgresql.Driver;

import ch.arons.jbench.pg.test.PGParameterCheck;


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

    public void prepare() {
        // TODO Auto-generated method stub
        
    }

    public void clean() {
        // TODO Auto-generated method stub
        
    }

    public void run() {
        // TODO Auto-generated method stub
        
    }


}