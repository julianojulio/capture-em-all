package jj.test.capture.em.all;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

@Test(enabled = false)
public class CaptureEmAllTest {
    private Path sourceFolder;
    private Path outputFolder;

    @BeforeMethod
    public void before() throws IOException {
        sourceFolder = Paths.get(CaptureEmAllTest.class.getResource("/").getPath());
        outputFolder = sourceFolder.resolve("output");

        Files.deleteIfExists(outputFolder);
        Files.createDirectories(outputFolder);
    }

    public void test_main() throws Exception {
        final File source = sourceFolder.resolve("true-story.png").toFile();

        CaptureEmAll.main(new String[]{source.getPath(), " -o=./output"});
        assertThat(Paths.get(getClass().getResource("/true-story.png").getPath())).exists();
//        assertThat(Paths.get(getClass().getResource("/output/true-story.png").getPath())).exists();
    }
}
