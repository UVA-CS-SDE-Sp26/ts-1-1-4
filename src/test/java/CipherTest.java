import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Cipher class.
 * Tests cipher key validation, loading, and deciphering functionality.
 *
 * Team Member D
 */
public class CipherTest {

    @TempDir
    Path tempDir;

    private File validKeyFile;
    // Caesar shift cipher with alphanumerics (62 characters)
    private String validActualLine = "abcdefghijklmnopqurstuvwxyzABCDEFGHIJKLMNOPQURSTUVWXYZ1234567890";
    private String validCipherLine = "bcdefghijklmnopqurstuvwxyzABCDEFGHIJKLMNOPQURSTUVWXYZ1234567890a";

    @BeforeEach
    public void setUp() throws IOException {
        // Create a valid key file for most tests
        validKeyFile = tempDir.resolve("key.txt").toFile();
        try (FileWriter writer = new FileWriter(validKeyFile)) {
            writer.write(validActualLine + "\n");
            writer.write(validCipherLine + "\n");
        }
    }

    @Test
    public void testValidCipherKeyLoading() throws IOException {
        // Test that a valid cipher key loads without errors
        Cipher cipher = new Cipher(validKeyFile.getAbsolutePath());
        assertNotNull(cipher);
        assertEquals(validKeyFile.getAbsolutePath(), cipher.getKeyFilePath());
    }

    @Test
    public void testDecipherLowercaseLetter() throws IOException {
        // Test deciphering lowercase letters (Caesar shift: a->b means b deciphers to a)
        Cipher cipher = new Cipher(validKeyFile.getAbsolutePath());

        assertEquals("a", cipher.decipher("b"));
        assertEquals("b", cipher.decipher("c"));
        assertEquals("c", cipher.decipher("d"));
    }

    @Test
    public void testDecipherUppercaseLetter() throws IOException {
        // Test deciphering uppercase letters
        Cipher cipher = new Cipher(validKeyFile.getAbsolutePath());

        assertEquals("A", cipher.decipher("B"));
        assertEquals("B", cipher.decipher("C"));
        assertEquals("C", cipher.decipher("D"));
    }

    @Test
    public void testDecipherDigits() throws IOException {
        // Test deciphering digits
        Cipher cipher = new Cipher(validKeyFile.getAbsolutePath());

        assertEquals("1", cipher.decipher("2"));
        assertEquals("2", cipher.decipher("3"));
        assertEquals("0", cipher.decipher("a")); // Wraparound: 0->a means a deciphers to 0
    }

    @Test
    public void testDecipherMixedContent() throws IOException {
        // Test deciphering with mixed alphanumeric content
        Cipher cipher = new Cipher(validKeyFile.getAbsolutePath());

        String ciphered = "Ifmmp3";
        String expected = "Hello2";
        assertEquals(expected, cipher.decipher(ciphered));
    }

    @Test
    public void testPreserveNonKeyCharacters() throws IOException {
        // Test that characters not in the cipher key remain unchanged
        Cipher cipher = new Cipher(validKeyFile.getAbsolutePath());

        String ciphered = "Ifmmp, Xpsme! ";
        String deciphered = cipher.decipher(ciphered);

        // Verify comma, exclamation, and space are unchanged
        assertTrue(deciphered.contains(","));
        assertTrue(deciphered.contains("!"));
        assertTrue(deciphered.contains(" "));
    }

    @Test
    public void testDecipherEmptyString() throws IOException {
        // Test deciphering an empty string
        Cipher cipher = new Cipher(validKeyFile.getAbsolutePath());
        assertEquals("", cipher.decipher(""));
    }

    @Test
    public void testDecipherNullString() throws IOException {
        // Test deciphering null
        Cipher cipher = new Cipher(validKeyFile.getAbsolutePath());
        assertNull(cipher.decipher(null));
    }

    @Test
    public void testDecipherCompleteMessage() throws IOException {
        // Test deciphering a complete message
        Cipher cipher = new Cipher(validKeyFile.getAbsolutePath());

        String ciphered = "Uijt jt b tfdsfu nfttbhf!";
        String deciphered = cipher.decipher(ciphered);

        // Should preserve structure (spaces, punctuation)
        assertEquals(ciphered.length(), deciphered.length());
        assertTrue(deciphered.contains(" "));
        assertTrue(deciphered.contains("!"));
    }

    @Test
    public void testCaesarShiftWraparound() throws IOException {
        // Test the wraparound behavior (z->A, Z->1, 0->a)
        Cipher cipher = new Cipher(validKeyFile.getAbsolutePath());

        assertEquals("z", cipher.decipher("A")); // lowercase z wraps to uppercase A
        assertEquals("Z", cipher.decipher("1")); // uppercase Z wraps to digit 1
        assertEquals("0", cipher.decipher("a")); // digit 0 wraps to lowercase a
    }

    @Test
    public void testMissingKeyFile() {
        // Test that missing key file throws IOException
        String nonExistentPath = tempDir.resolve("nonexistent.txt").toString();
        assertThrows(IOException.class, () -> {
            new Cipher(nonExistentPath);
        });
    }

    @Test
    public void testKeyFileWithOnlyOneLine() throws IOException {
        // Test that key file with only one line fails validation
        File invalidFile = tempDir.resolve("invalid1.txt").toFile();
        try (FileWriter writer = new FileWriter(invalidFile)) {
            writer.write(validActualLine + "\n");
        }

        assertThrows(IllegalArgumentException.class, () -> {
            new Cipher(invalidFile.getAbsolutePath());
        });
    }

    @Test
    public void testKeyFileWithThreeLines() throws IOException {
        // Test that key file with three lines fails validation
        File invalidFile = tempDir.resolve("invalid2.txt").toFile();
        try (FileWriter writer = new FileWriter(invalidFile)) {
            writer.write(validActualLine + "\n");
            writer.write(validCipherLine + "\n");
            writer.write("EXTRA LINE\n");
        }

        assertThrows(IllegalArgumentException.class, () -> {
            new Cipher(invalidFile.getAbsolutePath());
        });
    }

    @Test
    public void testKeyFileWithDifferentLineLengths() throws IOException {
        // Test that lines of different lengths fail validation
        File invalidFile = tempDir.resolve("invalid3.txt").toFile();
        try (FileWriter writer = new FileWriter(invalidFile)) {
            writer.write("abcdefghijklmnopqurstuvwxyz\n");
            writer.write("bcdefg\n"); // Too short
        }

        assertThrows(IllegalArgumentException.class, () -> {
            new Cipher(invalidFile.getAbsolutePath());
        });
    }

    @Test
    public void testKeyFileWithDuplicateCharactersInFirstLine() throws IOException {
        // Test that first line with duplicate characters fails validation
        File invalidFile = tempDir.resolve("invalid4.txt").toFile();
        try (FileWriter writer = new FileWriter(invalidFile)) {
            writer.write("abcdefghijklmnopqurstuvwxyza\n"); // 'a' appears twice
            writer.write("bcdefghijklmnopqurstuvwxyzab\n");
        }

        assertThrows(IllegalArgumentException.class, () -> {
            new Cipher(invalidFile.getAbsolutePath());
        });
    }

    @Test
    public void testKeyFileWithDuplicateCharactersInSecondLine() throws IOException {
        // Test that second line with duplicate characters fails validation
        File invalidFile = tempDir.resolve("invalid5.txt").toFile();
        try (FileWriter writer = new FileWriter(invalidFile)) {
            writer.write("abcdefghijklmnopqurstuvwxyz\n");
            writer.write("bcdefghijklmnopqurstuvwxyzb\n"); // 'b' appears twice
        }

        assertThrows(IllegalArgumentException.class, () -> {
            new Cipher(invalidFile.getAbsolutePath());
        });
    }

    @Test
    public void testAlternativeKeyFile() throws IOException {
        // Test using an alternative key file with different mapping
        File altKeyFile = tempDir.resolve("alternative_key.txt").toFile();

        // Simple reverse alphabet cipher (lowercase only)
        String altActual = "abcdefghijklmnopqrstuvwxyz";
        String altCipher = "zyxwvutsrqponmlkjihgfedcba";

        try (FileWriter writer = new FileWriter(altKeyFile)) {
            writer.write(altActual + "\n");
            writer.write(altCipher + "\n");
        }

        Cipher cipher = new Cipher(altKeyFile.getAbsolutePath());

        // With reverse alphabet: a->z, so z deciphers to a
        assertEquals("a", cipher.decipher("z"));
        assertEquals("z", cipher.decipher("a"));
        assertEquals("hello", cipher.decipher("svool"));
    }

    @Test
    public void testShortKeyFile() throws IOException {
        // Test that shorter cipher keys work (not required to be 62 chars)
        File shortKeyFile = tempDir.resolve("short_key.txt").toFile();

        String shortActual = "abc";
        String shortCipher = "bcd";

        try (FileWriter writer = new FileWriter(shortKeyFile)) {
            writer.write(shortActual + "\n");
            writer.write(shortCipher + "\n");
        }

        Cipher cipher = new Cipher(shortKeyFile.getAbsolutePath());

        assertEquals("a", cipher.decipher("b"));
        assertEquals("b", cipher.decipher("c"));
        assertEquals("c", cipher.decipher("d"));

        // Characters not in key should remain unchanged
        assertEquals("xyz", cipher.decipher("xyz"));
    }

    @Test
    public void testEmptyKeyFile() throws IOException {
        // Test that empty file fails validation
        File emptyFile = tempDir.resolve("empty.txt").toFile();
        emptyFile.createNewFile();

        assertThrows(IllegalArgumentException.class, () -> {
            new Cipher(emptyFile.getAbsolutePath());
        });
    }

    @Test
    public void testKeyFileWithOnlyNewlines() throws IOException {
        // Test file with only newline characters
        File newlineFile = tempDir.resolve("newlines.txt").toFile();
        try (FileWriter writer = new FileWriter(newlineFile)) {
            writer.write("\n\n");
        }

        assertThrows(IllegalArgumentException.class, () -> {
            new Cipher(newlineFile.getAbsolutePath());
        });
    }

    @Test
    public void testActualProvidedKey() throws IOException {
        // Test with the actual provided key from the assignment
        File actualKeyFile = tempDir.resolve("actual_key.txt").toFile();
        try (FileWriter writer = new FileWriter(actualKeyFile)) {
            writer.write("abcdefghijklmnopqurstuvwxyzABCDEFGHIJKLMNOPQURSTUVWXYZ1234567890\n");
            writer.write("bcdefghijklmnopqurstuvwxyzABCDEFGHIJKLMNOPQURSTUVWXYZ1234567890a\n");
        }

        Cipher cipher = new Cipher(actualKeyFile.getAbsolutePath());

        // Test various decryptions
        assertEquals("a", cipher.decipher("b"));
        assertEquals("z", cipher.decipher("A"));
        assertEquals("A", cipher.decipher("B"));
        assertEquals("Z", cipher.decipher("1"));
        assertEquals("9", cipher.decipher("0"));
        assertEquals("0", cipher.decipher("a"));
    }
}
