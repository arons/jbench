package ch.arons.jbench.pg;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Store connection information.
 */
public class DB {
    
    private final String url;
    private final Properties dbprops = new Properties();
    
    /**
     * create connection to db.
     */
    public DB(String url, String user, String pw) {
        this.url = url;
        dbprops.setProperty("user", user);
        dbprops.setProperty("password", pw);
        
        /*
         * JDBC batch API, that is PreparedStatement#addBatch(), enables the driver to send multiple "query executions" in a single network round trip. 
         * The current implementation, however, would still split large batches into smaller ones to avoid transmission control protocol (TCP) deadlock.
         * 
         * To prevent the TCP deadlock from being split into multiple blocks, after reWriteBatchedInserts is enabled, insert into () values (),(),()... is supported.
         * 
         * References:
         * 
         * https://stackoverflow.com/questions/47664889/jdbc-batch-operations-understanding/48349524?spm=a2c65.11461447.0.0.717db801z6JtyG#48349524
         * 
         * https://vladmihalcea.com/postgresql-multi-row-insert-rewritebatchedinserts-property/?spm=a2c65.11461447.0.0.717db801z6JtyG
         * 
         * https://jdbc.postgresql.org/documentation/head/connect.html?spm=a2c65.11461447.0.0.717db801z6JtyG
         */
        dbprops.setProperty("reWriteBatchedInserts", "true");
        
        //dbprops.setProperty("statementCacheSize", "0");
        

    }
    
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, dbprops);
    }

    
}
