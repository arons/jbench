package ch.arons.jbench.utils;

/**
 * Store single thread result.
 */
public class TestResul {
    
    public int numberThread;
    public long executions = 0;
    public long statements = 0;
    public long durationSelect = 0;
    public long durationUpdate = 0;
    public long durationInsert = 0;
    public long durationCommit = 0;
    public long durationRuntime = 0;

    /**
     * Print results.
     */
    public void print() {
        System.out.printf("Parallel client testing results for Treadh:%d\n", numberThread);
        System.out.printf("  Executions: %d \n", executions);
        System.out.printf("  Executions avg client: %f \n", executions / (double) numberThread);
        System.out.printf("  Statements: %d, per second: %f, per second and client: %f\n", statements,
                ((double) statements / durationRuntime * 1000L),
                ((double) statements / durationRuntime * 1000L / numberThread));
        System.out.printf("  Select avg: %f ms \n", durationSelect / (double) executions);
        System.out.printf("  Insert avg: %f ms \n", durationInsert / (double) executions);
        System.out.printf("  Commit avg: %f ms \n", durationCommit / (double) executions);
        System.out.printf("  Update avg: %f ms \n", durationUpdate / (double) executions);
        System.out.printf("  Runtime max: %d ms \n", durationRuntime);
    }
}