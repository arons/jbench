package ch.arons.jbench;

import ch.arons.jbench.info.Info;
import ch.arons.jbench.io.IO;

public class Main {

    private static void printHelp() {
        System.err.println("Usage: jbench (info,io) ");
        System.err.println("commands:");
        System.err.println(" info :  print some system info");
        System.err.println(" io   :  performs file system read/write test");
    }

    public static void main(String[] args) {
        Print.logVersion();
        
        
        if(args == null || args.length < 1) {
            printHelp();
            System.exit(0);
        }

        if("-h".equals(args[0]) || "--help".equals(args[0])) {
            Info.main(args);
        }
        
        if("info".equals(args[0])) {
            Info.main(args);
        }
        
        if("io".equals(args[0])) {
            IO.main(args);
        }
        
        
        printHelp();
        System.exit(0);
    }
}
