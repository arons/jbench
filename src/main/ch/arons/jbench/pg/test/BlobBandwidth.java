package ch.arons.jbench.pg.test;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.UUID;

import javax.management.timer.Timer;

import ch.arons.jbench.pg.DB;
import ch.arons.jbench.utils.BlobStream;

/**
 * test upload, internal copy and download bandwidth.
 */
public class BlobBandwidth extends SingleConDBTest {

    private static final int size = 20 * 1024 * 1024; //20 MG
    
    public BlobBandwidth(DB db) {
        super(db);
    }

    @Override
    protected void test(Connection c) throws SQLException {
        System.out.printf("Test bynary data bandwidth:\n");
        
        String id = UUID.randomUUID().toString();
        String id2 = UUID.randomUUID().toString();
        
        
        /*
         * upload
         */
        try (PreparedStatement ps = c.prepareStatement("insert into tbbm_lob (id, doc) values (?, ?)");) {
            long start = System.currentTimeMillis();
            ps.setString(1, id2);
            ps.setNull(2, Types.OTHER);
            ps.executeUpdate();
            long duration = System.currentTimeMillis() - start;
            System.out.printf(" NULL BLOB insert: %d ms\n", duration);
            
            // create blob
            start = System.currentTimeMillis();
            ps.setString(1, id);
            ps.setBinaryStream(2, new BlobStream(size));
            ps.executeUpdate();
            duration = System.currentTimeMillis() - start;
            float mbps = (size / 1024f / 1024f) / ((float) duration / Timer.ONE_SECOND);
            System.out.printf(" BLOB upload: %f MB/s\n", mbps);
            
        }
        
        /*
         * in db copy
         */
        try (PreparedStatement ps = c.prepareStatement("update tbbm_lob set doc = (select doc from tbbm_lob where id=?) where id=?");) {
            long start = System.currentTimeMillis();
            ps.setString(1, id);
            ps.setString(2, id2);
            ps.executeUpdate();
            long duration = System.currentTimeMillis() - start;
            float mbps = (size / 1024f / 1024f) / ((float) duration / Timer.ONE_SECOND);
            System.out.printf(" BLOB in-DB copy: %f MB/s\n", mbps);
        }
        
        /*
         * test download
         */
        try (PreparedStatement ps = c.prepareStatement("select doc from tbbm_lob where id = ?");) {
            long start = System.currentTimeMillis();
            ps.setString(1, id2);
            ResultSet rs = ps.executeQuery();
            rs.next();
            long bytes = 0;
            try (InputStream is = rs.getBinaryStream(1)) {
                byte[] buf = new byte[4096];
                int len = 0;
                while ((len = is.read(buf)) != -1) {
                    bytes += len;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            rs.close();
            rs = null;
            long duration = System.currentTimeMillis() - start;
            float mbps = (bytes / 1024f / 1024f) / ((float) duration / Timer.ONE_SECOND);
            System.out.printf(" BLOB download: %f MB/s\n", mbps);
        }
        
        /*
         * clean db
         */
        try (PreparedStatement ps = c.prepareStatement("delete from tbbm_lob where id = ?");) {
            ps.setString(1, id);
            ps.executeUpdate();
            ps.setString(1, id2);
            ps.executeUpdate();
        }
        
    }

}
