package ch.arons.jbench.pg.test.parallel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import ch.arons.jbench.pg.DB;
import ch.arons.jbench.pg.test.DBTest;
import ch.arons.jbench.utils.StringUtils;
import ch.arons.jbench.utils.TestResul;

/**
 * It start threads that simulate concurrency against the db.
 */
public class ParallelOperations extends DBTest {

    
    private static final int numberOperations = 5000;
    private static final int commitOperations = 10;
    private static final int runtimeSeconds = 30;
    private int maxParallel = 8;

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
        System.out.printf("Start Parallel client tests...\n"); 
        
        int currentThread = 1;
        List<TestResul> resultList =  new ArrayList<>(16);
        while (currentThread <= maxParallel) {
            TestResul result = singleRun(currentThread, runtimeSeconds);
            resultList.add(result);
            
            if (currentThread <= 1 ) {
                currentThread = 4;
            } else if (currentThread == 4 ) {
                currentThread = 8;
            } else if (currentThread == 8 ) {
                currentThread = 10;
            } else if (currentThread == 10 ) {
                currentThread = 12;
            } else if (currentThread == 12 ) {
                currentThread = 14;
            } else if (currentThread == 14 ) {
                currentThread = 16;
            } else {
                currentThread += 16;
            } 
        }
        
        for (TestResul result : resultList) {
            result.print();
        }
        
    }
    
    
    
    private TestResul singleRun(int numberThread, int runtimeSeconds) {
        long startMs = System.currentTimeMillis();
        
        ExecutorService exePull = Executors.newFixedThreadPool(numberThread);
        
        System.out.printf(" Client size:%d, max client operations: %d, commit operation:%d,  max running seconds: %d. ", 
                          numberThread, numberOperations, commitOperations, runtimeSeconds);
        
        List<SingleClient> clientList = new ArrayList<>(numberThread);
        int chunk = 1_000_000 / numberThread;
        for (int i = 0; i < numberThread; i++) {
            SingleClient client = new SingleClient(db, i * chunk, (i + 1) * chunk, numberOperations, commitOperations);
            clientList.add(client);
            exePull.execute(client);
        }
        
        
        try {
            
            System.out.printf(" running...");
            
            exePull.shutdown();
            if (!exePull.awaitTermination(runtimeSeconds, TimeUnit.SECONDS)) {
                System.out.printf(" shutdown...");
                
                
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
        
        
        
        TestResul result = new TestResul();
        result.numberThread = numberThread;
        
        for (SingleClient c :  clientList) {
            result.executions += c.operations;
            result.statements += c.statements;
            result.durationSelect += c.durationSelect;
            result.durationUpdate += c.durationUpdate;
            result.durationInsert += c.durationInsert;
            result.durationCommit += c.durationCommit;
            result.durationRuntime = Math.max(result.durationRuntime, c.durationRuntime);
        }
        
        
        System.out.printf(" end. ms:%d\n", (System.currentTimeMillis() - startMs));
        return result;
        
    }
}
