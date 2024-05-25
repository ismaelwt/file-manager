import com.challenge.service.Process;
import com.challenge.service.ShoppingService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;

public class MainTest {

    private static final String FILE_INPUT = "files";
    private static final String FILE_OUTPUT = "out";

    @DisplayName("Test loading a JSON file")
    @Test
    void loadJSONTest() throws URISyntaxException, IOException {

        var process = new Process(new ShoppingService());
        List<Path> pathsFromResourceJAR = process.getPathsFromResourceJAR(FILE_INPUT);

        for (Path path : pathsFromResourceJAR) {

            String filePathInJAR = path.toString();
            var fileName = filePathInJAR.substring(filePathInJAR.length() - 10, filePathInJAR.length());
            if (filePathInJAR.startsWith("/")) {
                filePathInJAR = filePathInJAR.substring(1, filePathInJAR.length());
            }

            InputStream is = process.getFileFromResourceAsStream(FILE_INPUT + "/" + fileName);
            System.out.println("##### -------- INICIO DO PROCESSO " + fileName + " --------#######");
            process.createFile(process.doProcess(is), fileName);
        }

        var result = new File(System.getProperty("user.dir") + System.getProperty("file.separator") + FILE_OUTPUT).listFiles();

        Assertions.assertEquals(result.length, 2);
    }
}
