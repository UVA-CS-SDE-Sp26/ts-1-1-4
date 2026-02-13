import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static org.junit.jupiter.api.Assertions.*;

// Tests for FileHandler file listing and reading
class FileHandlerTest {
    @TempDir
    Path tempDir;

    private File dataDir;
    private FileHandler fileHandler;
    private File projectDataDir;

    @BeforeEach
    void setUp() throws IOException {
        // Create data directory in project root for tests
        projectDataDir = new File("data");
        if (!projectDataDir.exists()) {
            projectDataDir.mkdirs();
        }

        // Copy temp dir structure to project root
        dataDir = tempDir.resolve("data").toFile();
        dataDir.mkdirs();

        fileHandler = new FileHandler();
    }

    @AfterEach
    void tearDown() throws IOException {
        // Clean up: remove test files but keep directory
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

    // Creates test file in project data directory
    private void createTestFile(String filename, String content) throws IOException {
        File testFile = new File(projectDataDir, filename);
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write(content);
        }
    }

    @Test
    @DisplayName("listDataFiles returns sorted list")
    void testListDataFilesReturnsSortedList() throws IOException {
        createTestFile("fileb.txt", "content");
        createTestFile("filea.txt", "content");
        var files = fileHandler.listDataFiles();
        assertEquals(2, files.size());
        assertEquals("filea.txt", files.get(0));
        assertEquals("fileb.txt", files.get(1));
    }

    @Test
    @DisplayName("listDataFiles returns empty list for empty directory")
    void testListDataFilesWithEmptyDirectory() {
        var files = fileHandler.listDataFiles();
        assertTrue(files.isEmpty());
    }

    @Test
    @DisplayName("listDataFiles excludes directories")
    void testListDataFilesExcludesDirectories() throws IOException {
        createTestFile("filea.txt", "content");
        File subdir = new File(projectDataDir, "subdir");
        subdir.mkdirs();
        var files = fileHandler.listDataFiles();
        assertEquals(1, files.size());
        assertFalse(files.contains("subdir"));
        subdir.delete();
    }

    @Test
    @DisplayName("listDataFiles handles missing directory")
    void testListDataFilesHandlesMissingDirectory() {
        File backupDir = projectDataDir;
        projectDataDir.renameTo(new File("data_backup"));
        try {
            assertThrows(RuntimeException.class, () -> fileHandler.listDataFiles());
        } finally {
            new File("data_backup").renameTo(backupDir);
        }
    }

    @Test
    @DisplayName("readDataFile returns file contents")
    void testReadDataFileReturnsFileContents() throws IOException {
        createTestFile("test.txt", "File content");
        String result = fileHandler.readDataFile("test.txt");
        assertEquals("File content\n", result);
    }

    @Test
    @DisplayName("readDataFile preserves line breaks")
    void testReadDataFilePreservesLineBreaks() throws IOException {
        createTestFile("multiline.txt", "Line 1\nLine 2");
        String result = fileHandler.readDataFile("multiline.txt");
        assertTrue(result.contains("Line 1"));
        assertTrue(result.contains("Line 2"));
    }

    @Test
    @DisplayName("readDataFile rejects null filename")
    void testReadDataFileRejectsNullFilename() {
        assertThrows(IllegalArgumentException.class, () -> fileHandler.readDataFile(null));
    }

    @Test
    @DisplayName("readDataFile rejects path traversal")
    void testReadDataFileRejectsPathTraversal() {
        assertThrows(IllegalArgumentException.class, () -> fileHandler.readDataFile("../file.txt"));
        assertThrows(IllegalArgumentException.class, () -> fileHandler.readDataFile("data/file.txt"));
    }

    @Test
    @DisplayName("readDataFile handles missing file")
    void testReadDataFileHandlesMissingFile() {
        assertThrows(RuntimeException.class, () -> fileHandler.readDataFile("nonexistent.txt"));
    }
}
