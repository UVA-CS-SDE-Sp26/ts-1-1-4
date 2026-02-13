import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * Team Member B
 * All direct file access lives here.
 *
 * Responsibilities:
 * - List files in the /data directory
 * - Read a specific file from /data
 *
 * Notes:
 * - Does NOT print to console (lets CLI handle messaging)
 * - Throws RuntimeException so ProgramController/CLI can handle gracefully
 */
public class FileHandler {

    private static final String DATA_DIR = "data";

    /**
     * Lists all regular files in the data/ directory, sorted by filename.
     *
     * @return sorted list of filenames
     * @throws RuntimeException if data directory is missing or unreadable
     */
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

    /**
     * Reads a file from the data/ directory and returns its raw contents.
     *
     * @param filename name like "filea.txt"
     * @return file contents with trailing newlines preserved line-by-line
     * @throws IllegalArgumentException if filename is null/blank or unsafe
     * @throws RuntimeException if the file does not exist or cannot be read
     */
    public String readDataFile(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            throw new IllegalArgumentException("Filename cannot be empty.");
        }

        // Prevent path traversal like "../secret.txt" or "data/filea.txt"
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
            // Should be rare since we check exists(), but keep it safe.
            throw new RuntimeException("Unable to open file: " + filename, e);
        }

        return sb.toString();
    }
}
