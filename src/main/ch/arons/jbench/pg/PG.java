package ch.arons.jbench.pg;

import java.sql.SQLException;
import java.util.Date;

import ch.arons.jbench.utils.StringUtils;

/**
 * Main test class for postgres tests.
 */
public class PG {
    
    private static void  logCurrentSystem() {
        System.out.printf("Host jvm processors: %d \n", Runtime.getRuntime().availableProcessors());
        System.out.printf("Total mem for JVM (MB): %d ", (Runtime.getRuntime().totalMemory() / (1024 * 1024)));
        System.out.printf("\n");
    }
    
    private static void printHelp() {
        System.err.println("jbench pg url user password (help|prepare|test|clean|params)");
        System.err.println("options:");
    }
    
    /**
     * main method. 
     */
    public static void main(String[] args) {
        
        if (args.length < 4) {
            printHelp();
            System.exit(0);
        }
        
        for (int i = 0; i < args.length; i++) {
            if ( "-h".equals(args[i]) || "--help".equals(args[i]) ) {
                printHelp();
                System.exit(0);
            }
        }
        
        String url = args[1];
        String user = args[2];
        String pw = args[3];
        
        
        String command = null;
        if ( args.length > 4 ) {
            command = args[4];
        }
        
        String additional = null;
        if ( args.length > 5 ) {
            additional = args[5];
        }
        
        if ( StringUtils.isEmpty(command) || "help".equalsIgnoreCase(command) ) {
            printHelp();
            System.exit(0);
        }
        
        logCurrentSystem();
        
        
        
        System.out.println("Start: " + new Date());
        long startMs = System.currentTimeMillis();
        try {
            PGTest pgtest = new PGTest(url, user, pw);
            pgtest.setAdditional(additional);
            
            if ("params".equalsIgnoreCase(command)) {
                pgtest.params();
            }
            
            if ("prepare".equalsIgnoreCase(command)) {
                pgtest.prepare();
            }
            
            if ("clean".equalsIgnoreCase(command)) {
                pgtest.clean();
            }
            
            if ("test".equalsIgnoreCase(command)) {
                pgtest.run();
            }
            
            if ("paral".equalsIgnoreCase(command)) {
                pgtest.runClient();
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        long endMs = System.currentTimeMillis();
        System.out.println("End: " + new Date());
        System.out.printf("Duration s: %s\n", (endMs - startMs) / 1000);
    }
}
