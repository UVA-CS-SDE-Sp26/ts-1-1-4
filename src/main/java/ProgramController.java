/**
 * Program Controller stub for TopSecret utility.
 * Team Member C is responsible for implementing this class.
 * 
 * This stub defines the interface that Member A's CLI expects.
 * Member C should implement the actual logic.
 */
public class ProgramController {

    /**
     * Returns a formatted list of available files.
     * 
     * @return A string containing numbered file list (e.g., "01 file.txt\n02 file2.txt")
     */
    public String listFiles() {
        // TODO: Member C implements this
        // Should call FileHandler to get available files
        // Should format as numbered list
        return "No files available (stub implementation)";
    }

    /**
     * Returns the contents of a file, deciphered with the default key.
     * 
     * @param fileNumber The file number to retrieve (e.g., "01")
     * @return The deciphered file contents
     * @throws RuntimeException if file not found or cannot be deciphered
     */
    public String getFileContents(String fileNumber) {
        // TODO: Member C implements this
        // Should call FileHandler to read file
        // Should call Cipher to decipher with default key
        return "File contents stub for file " + fileNumber;
    }

    /**
     * Returns the contents of a file, deciphered with a specified key.
     * 
     * @param fileNumber The file number to retrieve (e.g., "01")
     * @param keyPath    Path to the alternate cipher key file
     * @return The deciphered file contents
     * @throws RuntimeException if file or key not found, or cannot be deciphered
     */
    public String getFileContents(String fileNumber, String keyPath) {
        // TODO: Member C implements this
        // Should call FileHandler to read file
        // Should call Cipher to decipher with specified key
        return "File contents stub for file " + fileNumber + " with key " + keyPath;
    }
}
