package jj.test.capture.em.all.cli;

import jj.test.capture.em.all.Transaction;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.PrintStream;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ArgumentParser {

    private final CommandLineParser parser = new DefaultParser();
    private final Options options = new Options();
    final HelpFormatter formatter = new HelpFormatter();
    private final PrintStream printer;

    public ArgumentParser(final PrintStream printer) {
        this.printer = printer;
        options.addOption("o", "outputFolder", true, "output folder, current folder if not specified");
    }

    public List<Transaction> parse(final String... args) {
        try {
            final CommandLine line = parser.parse(options, args);

            final String outputFolder = line.hasOption('o') ? line.getOptionValue('o') : "download";
            final List<Transaction> transactions =
                    line.getArgList().stream().map(source -> new Transaction(source, outputFolder)).collect(Collectors.toList());

            if (transactions.isEmpty()) {
                printHelpMessage("No transactions provided.");
            }

            return transactions;
        } catch (final ParseException e) {
            printHelpMessage(e.getMessage());
            return Collections.emptyList();
        }
    }

    public void printHelpMessage(final String message) {
        printer.println(message);
        formatter.printHelp("java -jar capture-em-all.jar", options);
    }
}
