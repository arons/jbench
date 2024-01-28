package ch.arons.jbench;

public class Main {

    private static void printHelp() {
        Print.logVersion();
        System.err.println("options:");
    }

    public static void main(String[] args) {

        for (int i = 0; i < args.length; i++) {
            if ("-h".equals(args[i]) || "--help".equals(args[i])) {
                printHelp();
                System.exit(0);
            }
        }

    }
}
