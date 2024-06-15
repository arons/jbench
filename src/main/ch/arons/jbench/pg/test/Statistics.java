package ch.arons.jbench.pg.test;

import java.sql.Connection;
import java.sql.SQLException;

import ch.arons.jbench.pg.DB;
import ch.arons.jbench.pg.data.DataRun;

/**
 * Perform db statistic before any tests.
 */
public class Statistics extends SingleConDBTest {

    public Statistics(DB db) {
        super(db);
    }

    @Override
    public void test(Connection conn) throws SQLException {
        System.out.printf("Perform statistics");
        long startMs = System.currentTimeMillis();
        DataRun.executeQuery(conn, "analyze jbench.TBBM_PARENT");
        DataRun.executeQuery(conn, "analyze jbench.TBBM_CHILD");
        long endMs = System.currentTimeMillis();
        System.out.printf(", ms: %d\n", (endMs - startMs));
    }

}
