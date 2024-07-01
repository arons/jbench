package ch.arons.jbench.utils;

import ch.arons.jbench.pg.test.parallel.SingleClient;

/**
 * Store single thread result.
 */
public class TestResul {
    
    public int client;
    public int numberThread;
    public long executions = 0;
    public long statements = 0;
    
    public long statementsMax = 0;
    public long statementsMin = Long.MAX_VALUE;
    
    public long skipped = 0;
    public long durationSelect = 0;
    public long durationUpdate = 0;
    public long durationInsert = 0;
    public long durationCommit = 0;
    public long durationRuntime = 0;

    /**
     * Add a client in test result set.
     */
    public void addClient(SingleClient c) {
        this.client++;
        
        this.executions += c.operations;
        
        this.statements += c.statements;
        this.statementsMin = Math.min(this.statementsMin, c.statements);
        this.statementsMax = Math.max(this.statementsMax, c.statements);
        
        this.skipped += c.skipped;
        this.durationSelect += c.durationSelect;
        this.durationUpdate += c.durationUpdate;
        this.durationInsert += c.durationInsert;
        this.durationCommit += c.durationCommit;
        
        this.durationRuntime = Math.max(this.durationRuntime, c.durationRuntime);
    }
    
    /**
     * Print results.
     */
    public void print() {
        System.out.printf("Parallel client testing results for Treadh:%d\n", numberThread);
        System.out.printf("  Executions: %d , avg client: %f \n", executions, executions / (double) numberThread);
        
        System.out.printf("  Statements: %d (%d), avg: %d, min: %d max: %d \n", statements, skipped, statements / client, statementsMin, statementsMax);
        System.out.printf("  per second: %f, per second and client: %f\n",
                ((double) statements / durationRuntime * 1000L),
                ((double) statements / durationRuntime * 1000L / numberThread));
        
        System.out.printf("  Select avg: %f ms \n", durationSelect / (double) executions);
        System.out.printf("  Insert avg: %f ms \n", durationInsert / (double) executions);
        System.out.printf("  Commit avg: %f ms \n", durationCommit / (double) executions);
        System.out.printf("  Update avg: %f ms \n", durationUpdate / (double) executions);
        System.out.printf("  Runtime max: %d ms \n", durationRuntime);
    }

    
}