import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

// Tests for CommandLineInterface argument parsing and output
class CommandLineInterfaceTest {
    private CommandLineInterface cli;
    private MockProgramController mockController;
    private ByteArrayOutputStream outputStream;
    private ByteArrayOutputStream errorStream;
    private PrintStream originalOut;
    private PrintStream originalErr;

    @BeforeEach
    void setUp() {
        mockController = new MockProgramController();
        cli = new CommandLineInterface(mockController);
        outputStream = new ByteArrayOutputStream();
        errorStream = new ByteArrayOutputStream();
        originalOut = System.out;
        originalErr = System.err;
        System.setOut(new PrintStream(outputStream));
        System.setErr(new PrintStream(errorStream));
    }

    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    @DisplayName("No arguments calls listFiles")
    void testNoArgumentsCallsListFiles() {
        cli.run(new String[]{});
        assertTrue(mockController.listFilesCalled);
    }

    @Test
    @DisplayName("No arguments prints file list")
    void testNoArgumentsPrintsFileList() {
        mockController.listFilesResult = "01 file1.txt\n02 file2.txt";
        cli.run(new String[]{});
        assertEquals("01 file1.txt\n02 file2.txt", outputStream.toString().trim());
        tearDown();
    }

    @Test
    @DisplayName("One argument calls getFileContents")
    void testOneArgumentCallsGetFileContents() {
        cli.run(new String[]{"01"});
        assertTrue(mockController.getFileContentsCalled);
        assertEquals("01", mockController.lastFileNumber);
    }

    @Test
    @DisplayName("One argument prints contents")
    void testOneArgumentPrintsContents() {
        mockController.getFileContentsResult = "File contents";
        cli.run(new String[]{"02"});
        assertEquals("File contents", outputStream.toString().trim());
        tearDown();
    }

    @Test
    @DisplayName("Two arguments calls getFileContents with key")
    void testTwoArgumentsCallsGetFileContentsWithKey() {
        cli.run(new String[]{"01", "key.txt"});
        assertTrue(mockController.getFileContentsWithKeyCalled);
        assertEquals("01", mockController.lastFileNumber);
        assertEquals("key.txt", mockController.lastKeyPath);
    }

    @Test
    @DisplayName("Too many arguments prints error")
    void testTooManyArgumentsPrintsError() {
        cli.run(new String[]{"01", "key.txt", "extra"});
        assertTrue(errorStream.toString().contains("Too many arguments"));
        tearDown();
    }

    @Test
    @DisplayName("Non-numeric argument prints error")
    void testNonNumericFirstArgumentPrintsError() {
        cli.run(new String[]{"abc"});
        assertTrue(errorStream.toString().contains("must be a file number"));
        tearDown();
    }

    @Test
    @DisplayName("isValidNumber returns true for valid numbers")
    void testIsValidNumberWithValidNumbers() {
        assertTrue(cli.isValidNumber("1"));
        assertTrue(cli.isValidNumber("01"));
        assertTrue(cli.isValidNumber("123"));
    }

    @Test
    @DisplayName("isValidNumber returns false for invalid inputs")
    void testIsValidNumberWithInvalidInputs() {
        assertFalse(cli.isValidNumber(null));
        assertFalse(cli.isValidNumber(""));
        assertFalse(cli.isValidNumber("abc"));
    }

    @Test
    @DisplayName("Controller exception is caught and printed")
    void testControllerExceptionIsCaught() {
        mockController.shouldThrowException = true;
        mockController.exceptionMessage = "File not found";
        cli.run(new String[]{"99"});
        assertTrue(errorStream.toString().contains("File not found"));
        tearDown();
    }

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
        }

        @Override
        public String listFiles() {
            listFilesCalled = true;
            if (shouldThrowException) throw new RuntimeException(exceptionMessage);
            return listFilesResult;
        }

        @Override
        public String getFileContents(String fileNumber) {
            getFileContentsCalled = true;
            lastFileNumber = fileNumber;
            if (shouldThrowException) throw new RuntimeException(exceptionMessage);
            return getFileContentsResult;
        }

        @Override
        public String getFileContents(String fileNumber, String keyPath) {
            getFileContentsWithKeyCalled = true;
            lastFileNumber = fileNumber;
            lastKeyPath = keyPath;
            if (shouldThrowException) throw new RuntimeException(exceptionMessage);
            return getFileContentsWithKeyResult;
        }
    }
}
