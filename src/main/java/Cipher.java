import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Cipher class responsible for deciphering encrypted text using a substitution cipher.
 * The cipher key is loaded from a file and validated before use.
 * 
 * Team Member D
 */
public class Cipher {
    private Map<Character, Character> decipherMap;
    private String keyFilePath;
    
    /**
     * Constructor that loads and validates a cipher key from the specified file.
     * 
     * @param keyFilePath Path to the cipher key file
     * @throws IOException if the file cannot be read
     * @throws IllegalArgumentException if the key format is invalid
     */
    public Cipher(String keyFilePath) throws IOException {
        this.keyFilePath = keyFilePath;
        this.decipherMap = new HashMap<>();
        loadAndValidateKey();
    }
    
    /**
     * Loads the cipher key from file and validates its format.
     * 
     * @throws IOException if the file cannot be read
     * @throws IllegalArgumentException if the key format is invalid
     */
    private void loadAndValidateKey() throws IOException {
        String line1 = null;
        String line2 = null;
        
        // Read the two lines from the key file
        try (BufferedReader reader = new BufferedReader(new FileReader(keyFilePath))) {
            line1 = reader.readLine();
            line2 = reader.readLine();
            
            // Check if there are extra lines
            String extraLine = reader.readLine();
            if (extraLine != null) {
                throw new IllegalArgumentException("Cipher key file must contain exactly 2 lines");
            }
        }
        
        // Validate that both lines exist
        if (line1 == null || line2 == null) {
            throw new IllegalArgumentException("Cipher key file must contain exactly 2 lines");
        }
        
        // Validate that both lines have the same length
        if (line1.length() != line2.length()) {
            throw new IllegalArgumentException("Both lines in cipher key must have the same length");
        }
        
        // Validate that line 1 contains all unique characters
        validateUniqueness(line1, "First");
        
        // Validate that line 2 contains all unique characters
        validateUniqueness(line2, "Second");
        
        // Build the decipher map (reverse mapping: cipher -> actual)
        buildDecipherMap(line1, line2);
    }
    
    /**
     * Validates that all characters in a line are unique.
     * 
     * @param line The line to validate
     * @param lineName Name of the line for error messages ("First" or "Second")
     * @throws IllegalArgumentException if characters are duplicated
     */
    private void validateUniqueness(String line, String lineName) {
        Set<Character> seenChars = new HashSet<>();
        
        for (char c : line.toCharArray()) {
            if (seenChars.contains(c)) {
                throw new IllegalArgumentException(lineName + " line must contain unique characters");
            }
            seenChars.add(c);
        }
    }
    
    /**
     * Builds the decipher map from the cipher key.
     * Maps from cipher characters (line 2) to actual characters (line 1).
     * 
     * @param actualLine The actual characters (line 1)
     * @param cipherLine The cipher substitutions (line 2)
     */
    private void buildDecipherMap(String actualLine, String cipherLine) {
        for (int i = 0; i < actualLine.length(); i++) {
            char actual = actualLine.charAt(i);
            char cipher = cipherLine.charAt(i);
            
            // Map cipher character to actual character (deciphering)
            decipherMap.put(cipher, actual);
        }
    }
    
    /**
     * Deciphers the given ciphered text using the loaded cipher key.
     * Characters not in the cipher key remain unchanged.
     * 
     * @param cipheredText The encrypted text to decipher
     * @return The decrypted text
     */
    public String decipher(String cipheredText) {
        if (cipheredText == null) {
            return null;
        }
        
        StringBuilder deciphered = new StringBuilder();
        
        for (char c : cipheredText.toCharArray()) {
            if (decipherMap.containsKey(c)) {
                // Decipher the character
                deciphered.append(decipherMap.get(c));
            } else {
                // Keep characters not in cipher key unchanged
                deciphered.append(c);
            }
        }
        
        return deciphered.toString();
    }
    
    /**
     * Gets the path to the cipher key file.
     * 
     * @return The key file path
     */
    public String getKeyFilePath() {
        return keyFilePath;
    }
}
