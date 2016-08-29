package jj.test.capture.em.all;

import jj.test.capture.em.all.cli.ArgumentParser;
import jj.test.capture.em.all.core.Transaction;
import jj.test.capture.em.all.core.TransferManager;

import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CaptureEmAll {

    public static void main(final String[] args) throws Exception {
        final PrintStream printer = System.out;

        final ArgumentParser parser = new ArgumentParser(printer);

        final long startTime = System.currentTimeMillis();
        final List<Transaction> transactions = parser.parse(args);

        try (final TransferManager transferManager = new TransferManager(printer)) {
            transferManager.captureEmAll(transactions);

            while (!transferManager.isDone()) {
                TimeUnit.SECONDS.sleep(1);
            }
        }

        final long elapsedSeconds = TimeUnit.SECONDS.convert(System.currentTimeMillis() - startTime, TimeUnit.MILLISECONDS);
        printer.println(String.format("All transactions finished in %ss", elapsedSeconds));
    }
}
