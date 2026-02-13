import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;


class FileHandlerTest {
    @Mock
    private ProgramController mockProgramController;

    private FileHandler testFileHandler;
    public File testFile;


    @BeforeEach
    void setUp() {
       mockProgramController = mock(ProgramController.class);
    }


    @Test
    @DisplayName ("Correct content is read.")
    void testReadValidFile() throws Exception {

        testFileHandler.readFile("data/filea.txt");
        String content = testFileHandler.getData();

        assertEquals("Hello World, this is ts-1-1-4.", content);
    }

    @Test
    @DisplayName ("Test if file does not exist.")
    void testExistence() {
        testFileHandler = new FileHandler();

        assertThrows(IOException.class, () -> {
            testFileHandler.readFile("text.txt");
        });
    }


}