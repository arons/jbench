package ch.arons.jbench;

import java.util.Date;

public class Print {
    
    public static void  logVersion() {
        System.out.println("jbench "+Config.VERSION);
    }
    
    public static void  logTime() {
        System.out.println(new Date());
    }

    public static void  logCurrentSystem() {
        /* Total number of processors or cores available to the JVM */
        System.out.printf("Host jvm processors: %d \n", Runtime.getRuntime().availableProcessors());
        /* Total amount of free memory available to the JVM */
//      System.out.printf("Free mem (MB): %d ", (Runtime.getRuntime().freeMemory()/ (1024*1024)) );
        /* This will return Long.MAX_VALUE if there is no preset limit */
//      long maxMemory = Runtime.getRuntime().maxMemory();
        /* Maximum amount of memory the JVM will attempt to use */
//      System.out.printf("max mem (MB): %d \n" , (maxMemory == Long.MAX_VALUE ? "no limit" : (maxMemory / (1024*1024)) ));
        /* Total memory currently available to the JVM */
        System.out.printf("Total mem for JVM (MB): %d " , (Runtime.getRuntime().totalMemory() / (1024*1024)));
        System.out.printf("\n");
    }
}
