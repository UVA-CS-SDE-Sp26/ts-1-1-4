import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// Deciphers text using substitution cipher from key file
public class Cipher {
    private Map<Character, Character> decipherMap;
    private String keyFilePath;

    // Loads and validates cipher key from file
    public Cipher(String keyFilePath) throws IOException {
        this.keyFilePath = keyFilePath;
        this.decipherMap = new HashMap<>();
        loadAndValidateKey();
    }

    // Reads key file (2 lines), validates format, and builds decipher map
    private void loadAndValidateKey() throws IOException {
        String line1 = null;
        String line2 = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(keyFilePath))) {
            line1 = reader.readLine();
            line2 = reader.readLine();
            String extraLine = reader.readLine();
            if (extraLine != null) {
                throw new IllegalArgumentException("Cipher key file must contain exactly 2 lines");
            }
        }

        if (line1 == null || line2 == null) {
            throw new IllegalArgumentException("Cipher key file must contain exactly 2 lines");
        }

        if (line1.length() != line2.length()) {
            throw new IllegalArgumentException("Both lines in cipher key must have the same length");
        }

        validateUniqueness(line1, "First");
        validateUniqueness(line2, "Second");
        buildDecipherMap(line1, line2);
    }

    // Checks that all characters in a line are unique
    private void validateUniqueness(String line, String lineName) {
        Set<Character> seenChars = new HashSet<>();
        for (char c : line.toCharArray()) {
            if (seenChars.contains(c)) {
                throw new IllegalArgumentException(lineName + " line must contain unique characters");
            }
            seenChars.add(c);
        }
    }

    // Builds map from cipher characters to actual characters
    private void buildDecipherMap(String actualLine, String cipherLine) {
        for (int i = 0; i < actualLine.length(); i++) {
            char actual = actualLine.charAt(i);
            char cipher = cipherLine.charAt(i);
            decipherMap.put(cipher, actual);
        }
    }

    // Replaces each cipher character with actual character, leaves others unchanged
    public String decipher(String cipheredText) {
        if (cipheredText == null) {
            return null;
        }

        StringBuilder deciphered = new StringBuilder();
        for (char c : cipheredText.toCharArray()) {
            if (decipherMap.containsKey(c)) {
                deciphered.append(decipherMap.get(c));
            } else {
                deciphered.append(c);
            }
        }

        return deciphered.toString();
    }

    // Returns the key file path used by this cipher
    public String getKeyFilePath() {
        return keyFilePath;
    }
}
