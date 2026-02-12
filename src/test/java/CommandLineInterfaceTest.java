import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CommandLineInterface.
 * Team Member A is responsible for these tests.
 * 
 * Tests focus on:
 * - Argument parsing logic
 * - Input validation
 * - Error message handling
 * - Correct routing to controller methods
 */
class CommandLineInterfaceTest {

    private CommandLineInterface cli;
    private MockProgramController mockController;
    private ByteArrayOutputStream outputStream;
    private ByteArrayOutputStream errorStream;
    private PrintStream originalOut;
    private PrintStream originalErr;

    @BeforeEach
    void setUp() {
        // Create mock controller for testing
        mockController = new MockProgramController();
        cli = new CommandLineInterface(mockController);

        // Capture System.out and System.err for verification
        outputStream = new ByteArrayOutputStream();
        errorStream = new ByteArrayOutputStream();
        originalOut = System.out;
        originalErr = System.err;
        System.setOut(new PrintStream(outputStream));
        System.setErr(new PrintStream(errorStream));
    }

    void tearDown() {
        // Restore original streams
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    // ==================== No Arguments Tests ====================

    @Test
    @DisplayName("No arguments should call listFiles on controller")
    void testNoArgumentsCallsListFiles() {
        cli.run(new String[]{});
        assertTrue(mockController.listFilesCalled, "listFiles() should be called when no arguments provided");
    }

    @Test
    @DisplayName("No arguments should print file list to output")
    void testNoArgumentsPrintsFileList() {
        mockController.listFilesResult = "01 file1.txt\n02 file2.txt";
        cli.run(new String[]{});
        String output = outputStream.toString().trim();
        assertEquals("01 file1.txt\n02 file2.txt", output);
        tearDown();
    }

    // ==================== One Argument Tests ====================

    @Test
    @DisplayName("One valid argument should call getFileContents with file number")
    void testOneArgumentCallsGetFileContents() {
        cli.run(new String[]{"01"});
        assertTrue(mockController.getFileContentsCalled, "getFileContents() should be called with one argument");
        assertEquals("01", mockController.lastFileNumber);
        assertNull(mockController.lastKeyPath);
    }

    @Test
    @DisplayName("One argument should print file contents to output")
    void testOneArgumentPrintsContents() {
        mockController.getFileContentsResult = "Mission briefing contents here";
        cli.run(new String[]{"02"});
        String output = outputStream.toString().trim();
        assertEquals("Mission briefing contents here", output);
        tearDown();
    }

    @Test
    @DisplayName("One argument with different number formats should work")
    void testDifferentNumberFormats() {
        // Test with leading zeros
        cli.run(new String[]{"01"});
        assertTrue(mockController.getFileContentsCalled);
        
        mockController.reset();
        
        // Test without leading zeros
        cli.run(new String[]{"1"});
        assertTrue(mockController.getFileContentsCalled);
        
        mockController.reset();
        
        // Test larger numbers
        cli.run(new String[]{"123"});
        assertTrue(mockController.getFileContentsCalled);
    }

    // ==================== Two Arguments Tests ====================

    @Test
    @DisplayName("Two arguments should call getFileContents with file number and key path")
    void testTwoArgumentsCallsGetFileContentsWithKey() {
        cli.run(new String[]{"01", "ciphers/altkey.txt"});
        assertTrue(mockController.getFileContentsWithKeyCalled, 
                   "getFileContents(number, key) should be called with two arguments");
        assertEquals("01", mockController.lastFileNumber);
        assertEquals("ciphers/altkey.txt", mockController.lastKeyPath);
    }

    @Test
    @DisplayName("Two arguments should print file contents to output")
    void testTwoArgumentsPrintsContents() {
        mockController.getFileContentsWithKeyResult = "Deciphered with alternate key";
        cli.run(new String[]{"01", "mykey.txt"});
        String output = outputStream.toString().trim();
        assertEquals("Deciphered with alternate key", output);
        tearDown();
    }

    // ==================== Error Handling Tests ====================

    @Test
    @DisplayName("Too many arguments should print error")
    void testTooManyArgumentsPrintsError() {
        cli.run(new String[]{"01", "key.txt", "extra"});
        String error = errorStream.toString();
        assertTrue(error.contains("Too many arguments"), "Should print 'too many arguments' error");
        tearDown();
    }

    @Test
    @DisplayName("Non-numeric first argument should print error")
    void testNonNumericFirstArgumentPrintsError() {
        cli.run(new String[]{"abc"});
        String error = errorStream.toString();
        assertTrue(error.contains("must be a file number"), "Should print error about file number");
        tearDown();
    }

    @Test
    @DisplayName("Empty string argument should print error")
    void testEmptyStringArgumentPrintsError() {
        cli.run(new String[]{""});
        String error = errorStream.toString();
        assertTrue(error.contains("must be a file number"), "Should print error for empty string");
        tearDown();
    }

    // ==================== isValidNumber Tests ====================

    @Test
    @DisplayName("isValidNumber returns true for valid numbers")
    void testIsValidNumberWithValidNumbers() {
        assertTrue(cli.isValidNumber("1"));
        assertTrue(cli.isValidNumber("01"));
        assertTrue(cli.isValidNumber("123"));
        assertTrue(cli.isValidNumber("0"));
    }

    @Test
    @DisplayName("isValidNumber returns false for invalid inputs")
    void testIsValidNumberWithInvalidInputs() {
        assertFalse(cli.isValidNumber(null));
        assertFalse(cli.isValidNumber(""));
        assertFalse(cli.isValidNumber("abc"));
        assertFalse(cli.isValidNumber("1.5"));
        assertFalse(cli.isValidNumber("1a"));
    }

    // ==================== Controller Exception Handling ====================

    @Test
    @DisplayName("Controller exception should be caught and printed as error")
    void testControllerExceptionIsCaught() {
        mockController.shouldThrowException = true;
        mockController.exceptionMessage = "File not found";
        cli.run(new String[]{"99"});
        String error = errorStream.toString();
        assertTrue(error.contains("File not found"), "Should print controller exception message");
        tearDown();
    }

    // ==================== Mock Controller for Testing ====================

    /**
     * Mock implementation of ProgramController for testing CLI in isolation.
     */
    private static class MockProgramController extends ProgramController {
        boolean listFilesCalled = false;
        boolean getFileContentsCalled = false;
        boolean getFileContentsWithKeyCalled = false;
        String lastFileNumber = null;
        String lastKeyPath = null;
        
        String listFilesResult = "01 test.txt";
        String getFileContentsResult = "Test contents";
        String getFileContentsWithKeyResult = "Test contents with key";
        
        boolean shouldThrowException = false;
        String exceptionMessage = "Test exception";

        void reset() {
            listFilesCalled = false;
            getFileContentsCalled = false;
            getFileContentsWithKeyCalled = false;
            lastFileNumber = null;
            lastKeyPath = null;
            shouldThrowException = false;
        }

        @Override
        public String listFiles() {
            listFilesCalled = true;
            if (shouldThrowException) {
                throw new RuntimeException(exceptionMessage);
            }
            return listFilesResult;
        }

        @Override
        public String getFileContents(String fileNumber) {
            getFileContentsCalled = true;
            lastFileNumber = fileNumber;
            lastKeyPath = null;
            if (shouldThrowException) {
                throw new RuntimeException(exceptionMessage);
            }
            return getFileContentsResult;
        }

        @Override
        public String getFileContents(String fileNumber, String keyPath) {
            getFileContentsWithKeyCalled = true;
            lastFileNumber = fileNumber;
            lastKeyPath = keyPath;
            if (shouldThrowException) {
                throw new RuntimeException(exceptionMessage);
            }
            return getFileContentsWithKeyResult;
        }
    }
}
