import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;


class FileHandlerTest {
    @Mock
    private FileHandler MockFileHandler;


    @BeforeEach
    void setUp() {
        FileHandler MockFileHandler = mock(FileHandler.class);
    }

    @Test
    void readFile() {

    }

    @Test
    void getData() {
    }
}