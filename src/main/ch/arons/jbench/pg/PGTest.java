package ch.arons.jbench.pg;

import java.sql.DriverManager;
import java.sql.SQLException;

import org.postgresql.Driver;

import ch.arons.jbench.pg.data.DataClean;
import ch.arons.jbench.pg.data.DataCreate;
import ch.arons.jbench.pg.test.ArrayQuery;
import ch.arons.jbench.pg.test.BlobBandwidth;
import ch.arons.jbench.pg.test.DBTest;
import ch.arons.jbench.pg.test.LargeTable;
import ch.arons.jbench.pg.test.OpenLatency;
import ch.arons.jbench.pg.test.PGParameterCheck;
import ch.arons.jbench.pg.test.PrintInfo;
import ch.arons.jbench.pg.test.Statistics;
import ch.arons.jbench.pg.test.TemporaryTable;
import ch.arons.jbench.pg.test.TrivialQuery;
import ch.arons.jbench.pg.test.parallel.ParallelOperations;

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

    /**
     * run tests on db.
     */
    public void run() {
        DBTest[] tests = {
            new PrintInfo(db),
            new OpenLatency(db),
            new Statistics(db),
            new TrivialQuery(db),
            new BlobBandwidth(db),
            new LargeTable(db),
            new TemporaryTable(db),
            new ArrayQuery(db),
            
            new ParallelOperations(db, additional)
        };
        for (DBTest t : tests) {
            t.test();
        }
    }


    public void runClient() {
        ParallelOperations t = new ParallelOperations(db, additional);
        t.test();
    }


}
