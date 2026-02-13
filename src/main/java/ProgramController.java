import java.io.IOException;
import java.util.List;

public class ProgramController {

    private final FileHandler fh = new FileHandler();
    private static final String DEFAULT_KEY_PATH = "ciphers/key.txt";

    public String listFiles() {
        List<String> files = fh.listDataFiles();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < files.size(); i++) {
            String num = String.format("%02d", i + 1);
            sb.append(num).append(" ").append(files.get(i)).append("\n");
        }
        return sb.toString();
    }

    public String getFileContents(String fileNumber) {
        return getFileContents(fileNumber, DEFAULT_KEY_PATH);
    }

    public String getFileContents(String fileNumber, String keyPath) {
        // 1) normalize/validate file number
        int index = parseFileNumberToIndex(fileNumber);

        // 2) get sorted file list and pick filename
        List<String> files = fh.listDataFiles();
        if (index < 0 || index >= files.size()) {
            throw new RuntimeException("File number out of range: " + fileNumber);
        }
        String filename = files.get(index);

        // 3) read raw file content (ciphered or plain)
        String raw = fh.readDataFile(filename);

        // 4) decipher using Cipher + provided key
        // For 3-person teams, you can just return raw (or still run cipher if provided)
        try {
            Cipher cipher = new Cipher(keyPath);
            return cipher.decipher(raw);
        } catch (IOException e) {
            throw new RuntimeException("Unable to read cipher key: " + keyPath, e);
        } catch (IllegalArgumentException e) {
            // key validation failed
            throw new RuntimeException("Invalid cipher key file: " + keyPath + " (" + e.getMessage() + ")", e);
        }
    }

    /**
     * Converts "01" or "1" into a 0-based index.
     * "01" -> 0, "02" -> 1, etc.
     */
    private int parseFileNumberToIndex(String fileNumber) {
        if (fileNumber == null || fileNumber.trim().isEmpty()) {
            throw new RuntimeException("File number is required.");
        }

        String trimmed = fileNumber.trim();
        int n;
        try {
            n = Integer.parseInt(trimmed);
        } catch (NumberFormatException e) {
            throw new RuntimeException("File number must be a number: " + fileNumber);
        }

        if (n <= 0) {
            throw new RuntimeException("File number must be >= 1: " + fileNumber);
        }

        return n - 1;
    }
}
