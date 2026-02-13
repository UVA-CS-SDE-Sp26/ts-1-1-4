import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;


class FileHandlerTest {
    @Mock
    private FileHandler MockFileHandler;
    private File MockFile;


    @BeforeEach
    void setUp() {
        MockFileHandler = mock(FileHandler.class);
    }

    @Test
    @DisplayName ("filea.txt is a valid file.")
    void testFileAExists() {
        MockFile = new File("data/filea.txt");
        assertTrue(MockFile.exists());
    }

    @Test
    @DisplayName ("fileb.txt is a valid file.")
    void testFileBExists() {
        MockFile = new File("data/fileb.txt");
        assertTrue(MockFile.exists());
    }

    @Test
    @DisplayName ("text.txt is not a valid file.")
    void testNotFileExists() {
        MockFile = new File("text.txt");
        assertFalse(MockFile.exists());
    }

    @Test
    @DisplayName ("Correct content is read.")
    void testReadValidFile() throws Exception {

        MockFileHandler.readFile("data/filea.txt");
        String content = MockFileHandler.getData();

        assertEquals("Expected content", content);
    }


}