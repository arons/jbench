package ch.arons.jbench;

import java.util.Date;

/**
 * Useful class for printing informations.
 */
public class Print {
    
    public static void  logVersion() {
        System.out.println("jbench " + Config.VERSION);
    }
    
    public static void logTime() {
        System.out.println(new Date());
    }

    /**
     * Print basic system informations.
     */
    public static void  logCurrentSystem() {
        /* Total number of processors or cores available to the JVM */
        System.out.printf("Host jvm processors: %d \n", Runtime.getRuntime().availableProcessors());
        /* Total memory currently available to the JVM */
        System.out.printf("Total mem for JVM (MB): %d ", (Runtime.getRuntime().totalMemory() / (1024 * 1024)));
        System.out.printf("\n");
    }
}
