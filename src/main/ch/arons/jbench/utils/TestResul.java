package ch.arons.jbench.utils;

import ch.arons.jbench.pg.test.parallel.SingleClient;

/**
 * Store single thread result.
 */
public class TestResul {
    
    public int client;
    public int numberThread;
    public long transaction = 0;
    public long durationRuntime = 0;
    
    public long statements = 0;
    public long statementsMax = 0;
    public long statementsMin = Long.MAX_VALUE;
    
    
    public double tpsTot = 0;
    public double tpsMax = 0;
    public double tpsMin = Double.MAX_VALUE;
    
    public long skipped = 0;
    public long durationSelect = 0;
    public long durationUpdate = 0;
    public long durationInsert = 0;
    public long durationCommit = 0;
    
    public long durationSum = 0;

    /**
     * Add a client in test result set.
     */
    public void addClient(SingleClient c) {
        this.client++;
        
        this.transaction += c.transaction;
        
        double tps = c.transaction / ((double) c.durationRuntime / 1000.0);
        this.tpsTot += tps;
        this.tpsMin = Math.min(this.tpsMin, tps);
        this.tpsMax = Math.max(this.tpsMax, tps);
        
        this.statements += c.statements;
        this.statementsMin = Math.min(this.statementsMin, c.statements);
        this.statementsMax = Math.max(this.statementsMax, c.statements);
        
        this.skipped += c.skipped;
        this.durationSelect += c.durationSelect;
        this.durationUpdate += c.durationUpdate;
        this.durationInsert += c.durationInsert;
        this.durationCommit += c.durationCommit;
        
        this.durationSum += c.durationRuntime;
        
    }
    
    /**
     * Print results.
     */
    public void print() {
        System.out.printf("Parallel client testing results for Treadh:%d\n", numberThread);
        System.out.printf("  Transactions: %d , Statement: %d (%d)", transaction, statements, skipped );
        System.out.printf(" glob TPS: %f", transaction / ((double) durationRuntime / 1000.0));
        System.out.printf(" TPS per client avg: %f , min: %f max: %f \n", tpsTot / client, tpsMin, tpsMax);
        
        System.out.printf(" Statements per second: %f, per second and client: %f\n",
                ((double) statements / (durationRuntime * 1000L)),
                (((double) statements / (durationRuntime * 1000L)) / numberThread));
        
        double selPerS = 1000L / (durationSelect / ((double) transaction * 3));
        double insPerS = 1000L / (durationInsert / (double) transaction);
        double comPerS = 1000L / (durationCommit / (double) transaction);
        double updPerS = 1000L / (durationUpdate / (double) transaction);
        
        System.out.printf("  Select avg: %f ms  per sec: %f\n", durationSelect / ((double) transaction * 3),  selPerS);
        System.out.printf("  Insert avg: %f ms  per sec: %f\n", durationInsert / (double) transaction,  insPerS);
        System.out.printf("  Commit avg: %f ms  per sec: %f\n", durationCommit / (double) transaction,  comPerS);
        System.out.printf("  Update avg: %f ms  per sec: %f\n", durationUpdate / (double) transaction,  updPerS);
        
        System.out.printf("    operations per sec: %f\n", selPerS + insPerS + comPerS + updPerS);
        //System.out.printf("  Runtime max: %d ms \n", durationRuntime);
    }

    
}