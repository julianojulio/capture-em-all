package jj.test.capture.em.all.protocol;

import jj.test.capture.em.all.Transaction;
import jj.test.capture.em.all.Transfer;
import org.apache.commons.io.IOUtils;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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

            final long copiedBytes = IOUtils.copyLarge(
                    urlConnection.getInputStream(),
                    new BufferedOutputStream(new FileOutputStream(transaction.getDestination()))
            );

            return new Transfer(contentLength, copiedBytes, Transfer.Status.FINISHED);
        } catch (final IOException e) {
            return new Transfer(-1, -1, Transfer.Status.ERROR, e.getMessage());
        }
    }
}
