package ch.arons.jbench.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

/**
 * Random blob data stream.
 */
public class BlobStream extends InputStream {
    private Random rng = new Random();
    private int avail;

    public BlobStream(int length) {
        this.avail = length;
    }

    @Override
    public int read() throws IOException {
        if (avail > 0) {
            avail--;
            return rng.nextInt(256);
        } else {
            return -1;
        }
    }
    
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (len > avail) {
            len = avail;
        }
        if (len <= 0) {
            return -1;
        }
        avail -= len;
        if ((b.length == len) && (off == 0)) {
            rng.nextBytes(b);
        } else {
            byte[] tmp = new byte[len];
            rng.nextBytes(tmp);
            System.arraycopy(tmp, 0, b, off, len);
        }
        return len;
    }
    
    @Override
    public int available() throws IOException {
        return avail;
    }
    
    @Override
    public boolean markSupported() {
        return false;
    }

}
