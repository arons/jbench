package ch.arons.jbench;

import ch.arons.jbench.info.Info;
import ch.arons.jbench.io.IO;
import ch.arons.jbench.pg.PG;

public class Main {

    private static void printHelp() {
        System.err.println("Usage: jbench (info,io) ");
        System.err.println("commands:");
        System.err.println(" info :  print some system info");
        System.err.println(" io   :  performs file system read/write test");
        System.err.println(" pg   :  performs postgres tests");
    }

    public static void main(String[] args) {
        Print.logVersion();
        
        
        if(args == null || args.length < 1) {
            printHelp();
            System.exit(0);
        }

        if("-h".equals(args[0]) || "--help".equals(args[0])) {
            Info.main(args);
            System.exit(0);
        }
        
        if("info".equals(args[0])) {
            Info.main(args);
            System.exit(0);
        }
        
        if("io".equals(args[0])) {
            IO.main(args);
            System.exit(0);
        }
        
        if("pg".equals(args[0])) {
            PG.main(args);
            System.exit(0);
        }
        
        
        printHelp();
        System.exit(0);
    }
}
