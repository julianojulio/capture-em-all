package jj.test.capture.em.all;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@Test
public class CaptureEmAllTest {
    private final File resourcesFolder = new File(getClass().getResource("/").getPath());
    private final File outputFolder = resourcesFolder.toPath().resolve("output").toFile();

    @BeforeMethod
    public void cleanFolders() throws IOException {
        FileUtils.deleteQuietly(outputFolder);
        Files.createDirectories(outputFolder.toPath());
    }

    public void test_main() throws Exception {
        final File source = resourcesFolder.toPath().resolve("true-story.png").toFile();
        final File destination = outputFolder.toPath().resolve("true-story.png").toFile();

        assertThat(destination).doesNotExist();
        final String[] args = {source.toURI().toString(), "-o=" + outputFolder.toString()};
        System.out.println("Input: " + Arrays.toString(args));

        CaptureEmAll.main(args);
        assertThat(destination).exists();
    }
}
