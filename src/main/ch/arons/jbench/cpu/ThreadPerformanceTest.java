package ch.arons.jbench.cpu;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Test cpu operations.
 */
public class ThreadPerformanceTest {

    /**
     * main method.
     */
    public static void main(String[] args) throws InterruptedException {
        int maxThreads = 32;
        for (int numThreads = 1; numThreads <= maxThreads; numThreads *= 2) {
            runTest(numThreads);
        }
    }

    private static void runTest(int numThreads) throws InterruptedException {
        AtomicLong operationCount = new AtomicLong(0);
        Thread[] threads = new Thread[numThreads];
        long startTime = System.nanoTime();

        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 1000000; j++) { // Esegui un numero fisso di operazioni
                    operationCount.incrementAndGet();
                }
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        double seconds = duration / 1_000_000_000.0;

        System.out.printf("Threads: %d, Operations: %d, Time: %.2f seconds, Ops/sec: %.2f%n",
                numThreads, operationCount.get(), seconds, operationCount.get() / seconds);
    }
}
