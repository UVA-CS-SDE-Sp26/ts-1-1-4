import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

// Tests for Cipher key validation and deciphering
public class CipherTest {
    @TempDir
    Path tempDir;

    private File validKeyFile;
    private String validActualLine = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    private String validCipherLine = "bcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890a";

    @BeforeEach
    public void setUp() throws IOException {
        validKeyFile = tempDir.resolve("key.txt").toFile();
        try (FileWriter writer = new FileWriter(validKeyFile)) {
            writer.write(validActualLine + "\n");
            writer.write(validCipherLine + "\n");
        }
    }

    @Test
    public void testValidCipherKeyLoading() throws IOException {
        Cipher cipher = new Cipher(validKeyFile.getAbsolutePath());
        assertNotNull(cipher);
        assertEquals(validKeyFile.getAbsolutePath(), cipher.getKeyFilePath());
    }

    @Test
    public void testDecipherLowercaseLetter() throws IOException {
        Cipher cipher = new Cipher(validKeyFile.getAbsolutePath());
        assertEquals("a", cipher.decipher("b"));
        assertEquals("b", cipher.decipher("c"));
    }

    @Test
    public void testDecipherUppercaseLetter() throws IOException {
        Cipher cipher = new Cipher(validKeyFile.getAbsolutePath());
        assertEquals("A", cipher.decipher("B"));
        assertEquals("B", cipher.decipher("C"));
    }

    @Test
    public void testDecipherDigits() throws IOException {
        Cipher cipher = new Cipher(validKeyFile.getAbsolutePath());
        assertEquals("1", cipher.decipher("2"));
        assertEquals("0", cipher.decipher("a"));
    }

    @Test
    public void testDecipherMixedContent() throws IOException {
        Cipher cipher = new Cipher(validKeyFile.getAbsolutePath());
        assertEquals("Hello2", cipher.decipher("Ifmmp3"));
    }

    @Test
    public void testPreserveNonKeyCharacters() throws IOException {
        Cipher cipher = new Cipher(validKeyFile.getAbsolutePath());
        String deciphered = cipher.decipher("Ifmmp, Xpsme! ");
        assertTrue(deciphered.contains(","));
        assertTrue(deciphered.contains("!"));
        assertTrue(deciphered.contains(" "));
    }

    @Test
    public void testDecipherEmptyString() throws IOException {
        Cipher cipher = new Cipher(validKeyFile.getAbsolutePath());
        assertEquals("", cipher.decipher(""));
    }

    @Test
    public void testMissingKeyFile() {
        String nonExistentPath = tempDir.resolve("nonexistent.txt").toString();
        assertThrows(IOException.class, () -> new Cipher(nonExistentPath));
    }

    @Test
    public void testKeyFileWithOnlyOneLine() throws IOException {
        File invalidFile = tempDir.resolve("invalid1.txt").toFile();
        try (FileWriter writer = new FileWriter(invalidFile)) {
            writer.write(validActualLine + "\n");
        }
        assertThrows(IllegalArgumentException.class, () -> new Cipher(invalidFile.getAbsolutePath()));
    }

    @Test
    public void testKeyFileWithDifferentLineLengths() throws IOException {
        File invalidFile = tempDir.resolve("invalid3.txt").toFile();
        try (FileWriter writer = new FileWriter(invalidFile)) {
            writer.write("abcdefghijklmnopqrstuvwxyz\n");
            writer.write("bcdefg\n");
        }
        assertThrows(IllegalArgumentException.class, () -> new Cipher(invalidFile.getAbsolutePath()));
    }

    @Test
    public void testKeyFileWithDuplicateCharacters() throws IOException {
        File invalidFile = tempDir.resolve("invalid4.txt").toFile();
        try (FileWriter writer = new FileWriter(invalidFile)) {
            writer.write("abcdefghijklmnopqurstuvwxyza\n");
            writer.write("bcdefghijklmnopqurstuvwxyzab\n");
        }
        assertThrows(IllegalArgumentException.class, () -> new Cipher(invalidFile.getAbsolutePath()));
    }

    @Test
    public void testAlternativeKeyFile() throws IOException {
        File altKeyFile = tempDir.resolve("alternative_key.txt").toFile();
        try (FileWriter writer = new FileWriter(altKeyFile)) {
            writer.write("abcdefghijklmnopqrstuvwxyz\n");
            writer.write("zyxwvutsrqponmlkjihgfedcba\n");
        }
        Cipher cipher = new Cipher(altKeyFile.getAbsolutePath());
        assertEquals("a", cipher.decipher("z"));
        assertEquals("hello", cipher.decipher("svool"));
    }
}
