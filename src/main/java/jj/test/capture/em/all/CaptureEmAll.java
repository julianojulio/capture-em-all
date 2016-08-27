package jj.test.capture.em.all;

import jj.test.capture.em.all.cli.ArgumentParser;
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

        final TransferManager transferManager = new TransferManager(printer);
        transferManager.captureEmAll(transactions);

        while (transferManager.inProgress()) {
            TimeUnit.SECONDS.sleep(1);
        }

        final long elapsedSeconds = TimeUnit.SECONDS.convert(System.currentTimeMillis() - startTime, TimeUnit.MILLISECONDS);
        printer.println(String.format("All transactions finished in %ss", elapsedSeconds));
    }
}
