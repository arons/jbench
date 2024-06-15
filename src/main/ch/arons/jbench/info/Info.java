package ch.arons.jbench.info;

import java.io.File;

/**
 * Print system informations. 
 */
public class Info {

    /**
     * Main method.
     */
    public static void main(String[] args) {
        //System.out.println("Information about OS");
        System.out.println("Name: " + System.getProperty("os.name"));
        System.out.println("Version: " + System.getProperty("os.version"));
        System.out.println("Arch: " + System.getProperty("os.arch"));
        System.out.println("");
        System.out.println("Java version: " + System.getProperty("java.version"));
        System.out.println("Java vendor: " + System.getProperty("java.vendor"));
        System.out.println("Java home: " + System.getProperty("java.home"));
        System.out.println("");

        /* Total number of processors or cores available to the JVM */
        System.out.printf("Host jvm processors: %d \n", Runtime.getRuntime().availableProcessors());
        /* Total amount of free memory available to the JVM */
        System.out.printf("Free mem (MB): %d ", (Runtime.getRuntime().freeMemory() / (1024 * 1024)));
        /* This will return Long.MAX_VALUE if there is no preset limit */
        long maxMemory = Runtime.getRuntime().maxMemory();
        /* Maximum amount of memory the JVM will attempt to use */
        System.out.printf("max mem (MB): %d \n",
                (maxMemory == Long.MAX_VALUE ? "no limit" : (maxMemory / (1024 * 1024))));
        /* Total memory currently available to the JVM */
        System.out.printf("Total mem for JVM (MB): %d ", (Runtime.getRuntime().totalMemory() / (1024 * 1024)));
        System.out.printf("");

        /* Total number of processors or cores available to the JVM */
        System.out.println("Available processors (cores): " + Runtime.getRuntime().availableProcessors());

        System.out.println("");
        /* Get a list of all filesystem roots on this system */
        File[] roots = File.listRoots();
        
        /* For each filesystem root, print some info */
        for (File root : roots) {
            System.out.println("File system root: " + root.getAbsolutePath());
            System.out.println("Total space (MB): " + (root.getTotalSpace() / (1024 * 1024)));
            System.out.println("Free space (MB): " + (root.getFreeSpace() / (1024 * 1024)));
            System.out.println("Usable space (MB): " + (root.getUsableSpace() / (1024 * 1024)));
        }
    }
}
