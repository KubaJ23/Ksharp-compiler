package KSCompiler;

public class Global {
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_PURPLE = "\u001B[35m";

    public static void printLineSeperator(){
        System.out.println(ANSI_CYAN);
        System.out.println("====".repeat(50));
        System.out.println(ANSI_RESET);
    }
}
