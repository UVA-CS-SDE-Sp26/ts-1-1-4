// Parses command line args and displays output
public class CommandLineInterface {
    private ProgramController controller;

    public CommandLineInterface(ProgramController controller) {
        this.controller = controller;
    }

    // Routes args to appropriate handler: list files, display file, or error
    public void run(String[] args) {
        try {
            if (args.length == 0) {
                handleListFiles();
            } else if (args.length == 1) {
                handleDisplayFile(args[0], null);
            } else if (args.length == 2) {
                handleDisplayFile(args[0], args[1]);
            } else {
                printError("Too many arguments. Usage: java topsecret [number] [keyfile]");
            }
        } catch (Exception e) {
            printError(e.getMessage());
        }
    }

    // Gets file list from controller and prints it
    private void handleListFiles() {
        String fileList = controller.listFiles();
        printOutput(fileList);
    }

    // Validates file number, gets file contents (with optional key), and prints
    private void handleDisplayFile(String fileNumberStr, String keyPath) {
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

    // Checks if string is a valid integer
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

    // Prints to stdout
    private void printOutput(String message) {
        System.out.println(message);
    }

    // Prints error to stderr
    private void printError(String message) {
        System.err.println("Error: " + message);
    }
}
