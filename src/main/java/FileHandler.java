import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

// Handles all file access from data/ directory
public class FileHandler {
    private static final String DATA_DIR = "data";

    // Returns sorted list of all files in data/ directory
    public List<String> listDataFiles() {
        File dir = new File(DATA_DIR);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new RuntimeException("Data directory not found: " + DATA_DIR);
        }

        File[] files = dir.listFiles();
        if (files == null) {
            throw new RuntimeException("Unable to read data directory: " + DATA_DIR);
        }

        List<String> names = new ArrayList<>();
        for (File f : files) {
            if (f.isFile()) {
                names.add(f.getName());
            }
        }

        Collections.sort(names);
        return names;
    }

    // Reads file contents, validates filename to prevent path traversal
    public String readDataFile(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            throw new IllegalArgumentException("Filename cannot be empty.");
        }

        // Prevent path traversal attacks
        if (filename.contains("/") || filename.contains("\\") || filename.contains("..")) {
            throw new IllegalArgumentException("Invalid filename: " + filename);
        }

        File chosenFile = new File(DATA_DIR + File.separator + filename);
        if (!chosenFile.exists() || !chosenFile.isFile()) {
            throw new RuntimeException("File not found: " + filename);
        }

        StringBuilder sb = new StringBuilder();
        try (Scanner fileReader = new Scanner(chosenFile)) {
            while (fileReader.hasNextLine()) {
                sb.append(fileReader.nextLine()).append("\n");
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Unable to open file: " + filename, e);
        }

        return sb.toString();
    }
}
