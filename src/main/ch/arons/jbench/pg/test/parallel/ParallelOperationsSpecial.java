package ch.arons.jbench.pg.test.parallel;

import java.util.concurrent.atomic.AtomicLong;

import ch.arons.jbench.pg.DB;
import ch.arons.jbench.pg.data.DataCreate;
import ch.arons.jbench.pg.test.DBTest;
import ch.arons.jbench.utils.StringUtils;

/**
 * It start threads that simulate concurrency against the db.
 */
public class ParallelOperationsSpecial extends DBTest {

    
    private static final int commitOperations = 10;
    private static final int runtimeSeconds = 10;
    private int maxParallel = 64;

    /**
     * Init.
     * 
     */
    public ParallelOperationsSpecial(DB db, String additionalParam) {
        super(db);
        if (!StringUtils.isEmpty(additionalParam)) {
            try {
                maxParallel = Integer.valueOf(additionalParam);
            } catch (NumberFormatException e) {
                //nothing to do
            }
        }
    }
    
    
    @Override
    public void test() {
        
        
        int currentThread = 1;
        while (currentThread <= maxParallel) {
            try {
                runTest(currentThread);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            if (currentThread <= 1 ) {
                currentThread = 2;
            } else if (currentThread < 16 ) {
                currentThread += 2;
            } else {
                currentThread += 16;
            } 
        }
        
    }
    
    private void runTest(int numThreads) throws InterruptedException {
        AtomicLong operationCount = new AtomicLong(0);
        Thread[] threads = new Thread[numThreads];
        long startTime = System.nanoTime();

        int chunk = ( DataCreate.p_number * DataCreate.c_number ) / maxParallel;
        
        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(new SingleClientS(db, i * chunk, (i + 1) * chunk, operationCount));
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
