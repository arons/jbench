package ch.arons.jbench.pg.test.parallel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import ch.arons.jbench.pg.DB;
import ch.arons.jbench.pg.data.DataCreate;
import ch.arons.jbench.pg.test.DBTest;
import ch.arons.jbench.utils.StringUtils;
import ch.arons.jbench.utils.TestResul;

/**
 * It start threads that simulate concurrency against the db.
 */
public class ParallelOperations extends DBTest {

    
    private static final int numberOperations = 5000;
    private static final int commitOperations = 10;
    private static final int runtimeSeconds = 10;
    private int maxParallel = 64;

    /**
     * Init.
     * 
     */
    public ParallelOperations(DB db, String additionalParam) {
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
        
        System.out.printf("First filling buffer cache...\n");
        TestResul resultBoot = singleRun(maxParallel, runtimeSeconds);
        resultBoot.print();
        
        
        System.out.printf("Start Parallel client tests...\n");
        
        int currentThread = 1;
        List<TestResul> resultList =  new ArrayList<>(16);
        while (currentThread <= maxParallel) {
            TestResul result = singleRun(currentThread, runtimeSeconds);
            resultList.add(result);
            
            if (currentThread <= 1 ) {
                currentThread = 2;
            } else if (currentThread < 16 ) {
                currentThread += 2;
            } else {
                currentThread += 16;
            } 
        }
        
        for (TestResul result : resultList) {
            result.print();
        }
        
    }


    private TestResul singleRun(int numberThread, int runtimeSeconds) {
        
        ExecutorService exePull = Executors.newFixedThreadPool(numberThread);
        
        System.out.printf(" Client size:%d, max client operations: %d, commit operation:%d,  max running seconds: %d. ", 
                          numberThread, numberOperations, commitOperations, runtimeSeconds);
        
        
        List<SingleClient> clientList = new ArrayList<>(numberThread);
        
        int chunk = ( DataCreate.p_number * DataCreate.c_number ) / maxParallel;
        // int chunk = ( DataCreate.p_number * DataCreate.c_number ) / numberThread;
        for (int i = 0; i < numberThread; i++) {
            SingleClient client = new SingleClient(db, i * chunk, (i + 1) * chunk, numberOperations, commitOperations);
            clientList.add(client);
            exePull.submit(client);
        }
        
        System.out.printf(" running ...");
        long startMs = System.currentTimeMillis();
        try {
            exePull.shutdown();
            if (!exePull.awaitTermination(runtimeSeconds, TimeUnit.SECONDS)) {
                System.out.printf(" shutdown ...");
                
                
                for (SingleClient c :  clientList) {
                    c.interrupt();
                }
                
                exePull.shutdown();
                // Wait a while for tasks to respond to being cancelled
                if (!exePull.awaitTermination(60, TimeUnit.SECONDS)) {
                    System.err.print("Pool did not terminate");
                }
            }
            
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        long durationPoolmS = (System.currentTimeMillis() - startMs);
        System.out.printf(" end. ms:%d ", durationPoolmS);
        
        
        TestResul result = new TestResul();
        result.numberThread = numberThread;
        
        for (SingleClient c :  clientList) {
            result.addClient(c);
    
        }
        result.durationRuntime = Math.max(result.durationRuntime, durationPoolmS);
        
        if ( durationPoolmS > 1000 ) {
            System.out.printf(" statements:%d  per seconds:%d\n", result.statements, (result.statements / (durationPoolmS / 1000)) );
        } else {
            System.out.printf(" statements:%d  \n", result.statements );
        }
        
        
        
        return result;
        
    }
}
