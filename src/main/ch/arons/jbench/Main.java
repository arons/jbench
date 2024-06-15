package ch.arons.jbench;

import ch.arons.jbench.info.Info;
import ch.arons.jbench.io.IO;

public class Main {

    private static void printHelp() {
        Print.logVersion();
        System.err.println("Usage: jbench (info,io) ");
        System.err.println("commands:");
        System.err.println(" info :  print some system info");
        System.err.println(" io   :  performs file system read/write test");
    }

    public static void main(String[] args) {

        if(args == null || args.length < 1) {
            printHelp();
            System.exit(0);
        }

        for (int i = 0; i < args.length; i++) {
            if ("-h".equals(args[i]) || "--help".equals(args[i])) {
                printHelp();
                System.exit(0);
            }
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
