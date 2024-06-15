package ch.arons.jbench.pg.test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import ch.arons.jbench.pg.DB;

/**
 * Info about db version.
 */
public class PrintInfo extends SingleConDBTest {
    public PrintInfo(DB db) {
        super(db);
    }

    @Override
    protected void test(Connection c) throws SQLException {
        printInfoPostgres(c);
    }

    private void printInfoPostgres(Connection c) {
        try (PreparedStatement ps = c.prepareStatement("SELECT version()")) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                System.out.printf("%s\n", rs.getString(1));
            }    
        } catch (SQLException e) {
            //nothing to do
        }
    }

}
