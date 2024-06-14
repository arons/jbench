package ch.arons.jbench;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.Random;

/**
 * 
 */
public class IO {

    static final DecimalFormat DF = new DecimalFormat("###.##");
    
    private static void printHelp() {
        Print.logVersion();
        System.err.println("options:");
        System.err.println(" block_size=8 (kb)");
        System.err.println(" numOfBlocks=256");
    }

    public static void main(String[] args) {

        int blockSizeKb = 8;
        long numBlocks = 256 * 1024;
        
        for (int i = 0; i < args.length; i++) {
            if ( "-h".equals(args[i]) || "--help".equals(args[i]) ) {
                printHelp();
                System.exit(0);
            }
            
            if ( args[i].startsWith("block_size=") ) {
                blockSizeKb = Integer.parseInt( args[i].replace("block_size=","") );
            } 
            
            if ( args[i].startsWith("numOfBlocks=") ) {
                numBlocks = Integer.parseInt(  args[i].replace("numOfBlocks=","") );
            }
            
        }
        
        if( (numBlocks*blockSizeKb*Config.KILOBYTE) / Config.MEGABYTE  > 2 * 1024 ) {
            System.out.println("File cannot be bigger than 2GB");
            System.exit(0);
        }
        
        Print.logTime();
        Print.logCurrentSystem();
        
        System.out.println("Start IO test");
        System.out.println(" block_size="+blockSizeKb+" (kb)");
        System.out.println(" numOfBlocks="+numBlocks);
        
        System.out.println(" fileSize="+ ((numBlocks*blockSizeKb*Config.KILOBYTE) / Config.MEGABYTE )+" mb");
        
        File localDir = new File("./");
        File testFile = new File(localDir, "testdata.tmp");


        System.out.println("Location: " + localDir.getAbsolutePath());
        System.out.println("Total size : " + localDir.getTotalSpace() / Config.MEGABYTE + " mb");
        System.out.println("Usable : " + localDir.getUsableSpace() / Config.MEGABYTE + " mb");
        System.out.println("Free : " + localDir.getFreeSpace() / Config.MEGABYTE + " mb");

        // params
        writeTest(blockSizeKb, numBlocks, testFile);
        readTest(blockSizeKb, numBlocks, testFile);
        
        testFile.delete();
        
    }



    private static void writeTest(int blockSizeKb, long numBlocks, File testFile) {
        System.out.println("+++ writring test");

        //prepare data
        Random rd = new Random();
        int blockSize = blockSizeKb * Config.KILOBYTE;
        byte[] blockArr = new byte[blockSize];
        for (int b = 0; b < blockArr.length; b++) {
            if (b % 2 == 0) {
                blockArr[b] = (byte)rd.nextInt();
            }
        }

        long totalBytesWritten = 0;

        long startTime = System.nanoTime();
        try (RandomAccessFile raf = new RandomAccessFile(testFile, "rw")) {
            for (long b = 0; b < numBlocks; b++) {
                raf.seek(b * blockSize);
                raf.write(blockArr, 0, blockSize);
                totalBytesWritten += blockSize;
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } 
        long endTime = System.nanoTime();

        long elapsedTimeNs = endTime - startTime;
        double sec = elapsedTimeNs / (double) 1000000000;
        double mbWritten = totalBytesWritten / (double) Config.MEGABYTE;
        double bwMbSec = mbWritten / sec;
        System.out.println("write IO is " + bwMbSec + " MB/s");
        System.out.println(DF.format(mbWritten) + "MB written in " + DF.format(sec) + " sec");
    }
    
    
    private static void readTest(int blockSizeKb, long numBlocks, File testFile) {
        System.out.println("+++  reading test");
        int blockSize = blockSizeKb * Config.KILOBYTE;
        byte[] blockArr = new byte[blockSize];
        
        long totalBytesRead = 0;
        long startTime = System.nanoTime();
        try (RandomAccessFile raf = new RandomAccessFile(testFile, "r")) {

            for (int b = 0; b < numBlocks; b++) {
                raf.seek(b * blockSize);
                raf.readFully(blockArr, 0, blockSize);
                totalBytesRead += blockSize;
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } 
        long endTime = System.nanoTime();

        long elapsedTimeNs = endTime - startTime;
        double sec = elapsedTimeNs / (double) 1000000000;
        double mbRead = totalBytesRead / (double) Config.MEGABYTE;
        double bwMbSec = mbRead / sec;
        System.out.println("read IO is " + bwMbSec + " MB/s");
        System.out.println(DF.format(mbRead) + "MB written in " + DF.format(sec) + " sec");
    }

}