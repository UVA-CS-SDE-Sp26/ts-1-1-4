/**
 * Command Line Interface for TopSecret utility.
 * Team Member A is responsible for this class.
 * 
 * This class handles:
 * - Parsing command line arguments
 * - Validating user input
 * - Calling the ProgramController to get results
 * - Printing output or error messages to the console
 */
public class CommandLineInterface {

    // Reference to the program controller (provided by Member C)
    private ProgramController controller;

    /**
     * Constructor that accepts a ProgramController.
     * This allows for dependency injection and easier testing.
     * 
     * @param controller The program controller to use for operations
     */
    public CommandLineInterface(ProgramController controller) {
        this.controller = controller;
    }

    /**
     * Main entry point for the CLI.
     * Parses arguments and routes to appropriate action.
     * 
     * @param args Command line arguments from main()
     */
    public void run(String[] args) {
        try {
            if (args.length == 0) {
                // No arguments: list available files
                handleListFiles();
            } else if (args.length == 1) {
                // One argument: display file with default key
                handleDisplayFile(args[0], null);
            } else if (args.length == 2) {
                // Two arguments: display file with alternate key
                handleDisplayFile(args[0], args[1]);
            } else {
                // Too many arguments
                printError("Too many arguments. Usage: java topsecret [number] [keyfile]");
            }
        } catch (Exception e) {
            printError(e.getMessage());
        }
    }

    /**
     * Handles the case when no arguments are provided.
     * Requests file list from controller and prints it.
     */
    private void handleListFiles() {
        String fileList = controller.listFiles();
        printOutput(fileList);
    }

    /**
     * Handles displaying a file's contents.
     * 
     * @param fileNumberStr The file number as a string (e.g., "01")
     * @param keyPath       Optional path to alternate key file (null for default)
     */
    private void handleDisplayFile(String fileNumberStr, String keyPath) {
        // Validate that the first argument is a number
        if (!isValidNumber(fileNumberStr)) {
            printError("First argument must be a file number.");
            return;
        }

        String contents;
        if (keyPath == null) {
            contents = controller.getFileContents(fileNumberStr);
        } else {
            contents = controller.getFileContents(fileNumberStr, keyPath);
        }
        printOutput(contents);
    }

    /**
     * Validates that a string represents a valid file number.
     * 
     * @param str The string to validate
     * @return true if the string is a valid number, false otherwise
     */
    boolean isValidNumber(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Prints normal output to standard output.
     * 
     * @param message The message to print
     */
    private void printOutput(String message) {
        System.out.println(message);
    }

    /**
     * Prints error messages to standard error.
     * 
     * @param message The error message to print
     */
    private void printError(String message) {
        System.err.println("Error: " + message);
    }
}
