package jj.test.capture.em.all.protocol;

import jj.test.capture.em.all.core.Transaction;
import jj.test.capture.em.all.core.TransactionException;
import jj.test.capture.em.all.core.TransferStatus;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public abstract class BaseTransfer implements Transfer {

    protected final Transaction transaction;

    public BaseTransfer(final Transaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public TransferStatus start() {
        try {
            Files.createDirectories(transaction.getOutputPath());

            final long size = getSize();

            final InputStream inputStream = openInputStream(transaction.getPath());
            final File tempFile = File.createTempFile("capture-em-all_", transaction.getProtocol());

            final BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(tempFile));
            final long copiedBytes = IOUtils.copyLarge(
                    inputStream,
                    outputStream
            );
            inputStream.close();
            outputStream.close();

            close();

            // copy file to final destination
            FileUtils.moveFile(tempFile, transaction.getDestination());

            return new TransferStatus(size, copiedBytes, TransferStatus.Status.FINISHED, transaction.getSource());
        } catch (final IOException | TransactionException e) {
            return new TransferStatus(-1, -1, TransferStatus.Status.ERROR,
                    String.format("Error transferring %s: %s\n", transaction.getSource(), e.getMessage())
            );
        }
    }

    abstract InputStream openInputStream(final String path);

    abstract long getSize();

    abstract void close();
}
