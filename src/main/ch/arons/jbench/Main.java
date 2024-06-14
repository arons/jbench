package ch.arons.jbench;

public class Main {

    private static void printHelp() {
        Print.logVersion();
        System.err.println("Usage: jbench (io) ");
        System.err.println("commands:");
        System.err.println(" io :  performs file system read/write test");
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
        
        
        if("io".equals(args[0])) {
            IO.main(args);
        }
        
        
        printHelp();
        System.exit(0);
    }
}
