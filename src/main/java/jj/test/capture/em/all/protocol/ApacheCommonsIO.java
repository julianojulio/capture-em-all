package jj.test.capture.em.all.protocol;

import jj.test.capture.em.all.core.Transaction;
import jj.test.capture.em.all.core.TransactionException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;

/**
 * This is a default implementation for multiple protocols, using Apache Commons IO.
 * It supports HTTP, HTTPS and FTP.
 */
public class ApacheCommonsIO implements Protocol {

    public ApacheCommonsIO.ApacheCommonsIOTransfer newTransfer(final Transaction transaction) {
        return new ApacheCommonsIO.ApacheCommonsIOTransfer(transaction);
    }

    @Override
    public List<String> getKnownProtocols() {
        return Arrays.asList("http", "https", "ftp", "file");
    }


    public static class ApacheCommonsIOTransfer extends BaseTransfer {

        private URLConnection urlConnection;

        public ApacheCommonsIOTransfer(final Transaction transaction) {
            super(transaction);

            try {
                urlConnection = transaction.getUri().toURL().openConnection();
            } catch (final IOException e) {
                throw new TransactionException("Error open connection with: " + transaction.getSource(), e);
            }
        }

        @Override
        InputStream openInputStream(final String path) {
            try {
                return urlConnection.getInputStream();
            } catch (final IOException e) {
                throw new TransactionException("Error open connection with: " + transaction.getSource(), e);
            }
        }

        @Override
        long getSize() {
            return urlConnection.getContentLength();
        }

        @Override
        void close() {
        }
    }
}
