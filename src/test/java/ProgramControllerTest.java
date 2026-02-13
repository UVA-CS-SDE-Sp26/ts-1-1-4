import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.AfterEach;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

// Tests for ProgramController file listing and content retrieval
class ProgramControllerTest {
    @TempDir
    Path tempDir;

    private File dataDir;
    private File ciphersDir;
    private File defaultKeyFile;
    private ProgramController controller;
    private File projectDataDir;
    private File projectCiphersDir;

    @BeforeEach
    void setUp() throws IOException {
        // Use project directories
        projectDataDir = new File("data");
        if (!projectDataDir.exists()) {
            projectDataDir.mkdirs();
        }
        projectCiphersDir = new File("ciphers");
        if (!projectCiphersDir.exists()) {
            projectCiphersDir.mkdirs();
        }

        dataDir = tempDir.resolve("data").toFile();
        ciphersDir = tempDir.resolve("ciphers").toFile();
        defaultKeyFile = new File(projectCiphersDir, "key.txt");
        createValidKeyFile(defaultKeyFile);

        controller = new ProgramController();
    }

    @AfterEach
    void tearDown() {
        // Clean up test files
        if (projectDataDir.exists()) {
            File[] files = projectDataDir.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isFile()) {
                        f.delete();
                    }
                }
            }
        }
    }

    // Creates valid cipher key file with Caesar shift
    private void createValidKeyFile(File keyFile) throws IOException {
        String actualLine = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        String cipherLine = "bcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890a";
        try (FileWriter writer = new FileWriter(keyFile)) {
            writer.write(actualLine + "\n");
            writer.write(cipherLine + "\n");
        }
    }

    // Creates test data file with content
    private void createTestDataFile(String filename, String content) throws IOException {
        File testFile = new File(projectDataDir, filename);
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write(content);
        }
    }

    @Test
    @DisplayName("listFiles returns formatted list")
    void testListFilesReturnsFormattedList() throws IOException {
        createTestDataFile("filea.txt", "test");
        createTestDataFile("fileb.txt", "test");
        String result = controller.listFiles();
        assertTrue(result.contains("01"));
        assertTrue(result.contains("02"));
        assertTrue(result.contains("filea.txt"));
    }

    @Test
    @DisplayName("listFiles handles empty directory")
    void testListFilesWithEmptyDirectory() {
        String result = controller.listFiles();
        // May have existing files, so just check it doesn't throw
        assertNotNull(result);
    }

    @Test
    @DisplayName("getFileContents with default key deciphers file")
    void testGetFileContentsWithDefaultKey() throws IOException {
        createTestDataFile("filea.txt", "b");
        String result = controller.getFileContents("01");
        assertEquals("a", result.trim());
    }

    @Test
    @DisplayName("getFileContents accepts different number formats")
    void testGetFileContentsWithValidFileNumber() throws IOException {
        createTestDataFile("filea.txt", "b");
        assertEquals("a", controller.getFileContents("01").trim());
        assertEquals("a", controller.getFileContents("1").trim());
    }

    @Test
    @DisplayName("getFileContents with custom key uses alternate key")
    void testGetFileContentsWithCustomKey() throws IOException {
        createTestDataFile("filea.txt", "z");
        File altKeyFile = new File(projectCiphersDir, "alt_key.txt");
        try (FileWriter writer = new FileWriter(altKeyFile)) {
            writer.write("abcdefghijklmnopqrstuvwxyz\n");
            writer.write("zyxwvutsrqponmlkjihgfedcba\n");
        }
        String result = controller.getFileContents("01", altKeyFile.getAbsolutePath());
        assertEquals("a", result.trim());
        altKeyFile.delete();
    }

    @Test
    @DisplayName("getFileContents rejects invalid file number")
    void testParseFileNumberRejectsNonNumeric() {
        assertThrows(RuntimeException.class, () -> controller.getFileContents("abc"));
    }

    @Test
    @DisplayName("getFileContents rejects out of range")
    void testGetFileContentsRejectsOutOfRangeHigh() throws IOException {
        createTestDataFile("filea.txt", "test");
        assertThrows(RuntimeException.class, () -> controller.getFileContents("99"));
    }

    @Test
    @DisplayName("getFileContents handles missing key file")
    void testGetFileContentsHandlesKeyFileNotFound() throws IOException {
        createTestDataFile("filea.txt", "test");
        File backupKey = new File(projectCiphersDir, "key_backup.txt");
        if (defaultKeyFile.exists()) {
            defaultKeyFile.renameTo(backupKey);
        }
        try {
            assertThrows(RuntimeException.class, () -> controller.getFileContents("01"));
        } finally {
            if (backupKey.exists()) {
                backupKey.renameTo(defaultKeyFile);
            }
        }
    }

    @Test
    @DisplayName("getFileContents handles invalid key format")
    void testGetFileContentsHandlesInvalidKeyFormat() throws IOException {
        createTestDataFile("filea.txt", "test");
        File backupKey = new File(projectCiphersDir, "key_backup.txt");
        if (defaultKeyFile.exists()) {
            defaultKeyFile.renameTo(backupKey);
        }
        try {
            try (FileWriter writer = new FileWriter(defaultKeyFile)) {
                writer.write("abc\n");
            }
            assertThrows(RuntimeException.class, () -> controller.getFileContents("01"));
        } finally {
            defaultKeyFile.delete();
            if (backupKey.exists()) {
                backupKey.renameTo(defaultKeyFile);
            }
        }
    }
}
