package jj.test.capture.em.all;

import jj.test.capture.em.all.cli.ArgumentParser;
import jj.test.capture.em.all.core.Transaction;
import jj.test.capture.em.all.core.Transfer;
import jj.test.capture.em.all.core.TransferManager;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@ComponentScan
public class CaptureEmAll {

    public static void main(final String[] args) throws Exception {
        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(CaptureEmAll.class);
        final PrintStream printer = System.out;

        final ArgumentParser parser = new ArgumentParser(printer);

        final long startTime = System.currentTimeMillis();
        final List<Transaction> transactions = parser.parse(args);

        try (final TransferManager transferManager = new TransferManager(printer)) {
            transferManager.captureEmAll(transactions);

            while (!transferManager.isDone()) {
                TimeUnit.SECONDS.sleep(1);
                printer.println("Waiting 1 sec");
            }

            printFinished(printer, transferManager.getResults());
        }

        final long elapsedSeconds = TimeUnit.SECONDS.convert(System.currentTimeMillis() - startTime, TimeUnit.MILLISECONDS);
        printer.println(String.format("All transactions finished in %ss", elapsedSeconds));
    }

    public static void printFinished(final PrintStream printer, List<Transfer> transfers) {
        transfers.forEach(transfer -> printer.format("Complete: %s\n", transfer.getFeedback()));
    }
}
