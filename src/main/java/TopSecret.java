/**
 * TopSecret Command Line Utility
 * 
 * Main entry point for the application.
 * This class delegates to CommandLineInterface for argument handling.
 */
public class TopSecret {

    /**
     * Main method - entry point for the application.
     * 
     * Usage:
     *   java topsecret              - List available files
     *   java topsecret [number]     - Display file contents
     *   java topsecret [number] [key] - Display with alternate key
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        // Create the program controller (Member C's responsibility)
        ProgramController controller = new ProgramController();
        
        // Create the CLI and run it (Member A's responsibility)
        CommandLineInterface cli = new CommandLineInterface(controller);
        cli.run(args);
    }
}
