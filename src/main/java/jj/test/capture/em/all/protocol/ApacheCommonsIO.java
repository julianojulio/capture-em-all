package jj.test.capture.em.all.protocol;

import jj.test.capture.em.all.core.Transaction;
import jj.test.capture.em.all.core.Transfer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

/**
 * This is a default implementation for multiple protocols, using Apache Commons IO.
 * It supports HTTP, HTTPS and FTP.
 */
public class ApacheCommonsIO implements KnownProtocol {

    @Override
    public List<String> getKnownProtocols() {
        return Arrays.asList("http", "https", "ftp", "file");
    }

    @Override
    public Transfer transfer(final Transaction transaction) {
        try {
            Files.createDirectories(transaction.getOutputPath());

            final URLConnection urlConnection = transaction.getUri().toURL().openConnection();
            final int contentLength = urlConnection.getContentLength();

            final InputStream inputStream = urlConnection.getInputStream();
            final File tempFile = File.createTempFile("capture-em-all_", transaction.getProtocol());

            final BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(tempFile));
            final long copiedBytes = IOUtils.copyLarge(
                    inputStream,
                    outputStream
            );
            inputStream.close();
            outputStream.close();

            // copy file to final destination
            FileUtils.moveFile(tempFile, transaction.getDestination());

            return new Transfer(contentLength, copiedBytes, Transfer.Status.FINISHED, transaction.getSource());
        } catch (final IOException e) {
            return new Transfer(-1, -1, Transfer.Status.ERROR,
                    String.format("Error transferring %s: %s\n", transaction.getSource(), e.getMessage())
            );
        }
    }
}
