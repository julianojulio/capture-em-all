package jj.test.capture.em.all.cli;

import jj.test.capture.em.all.Transaction;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ArgumentParser {

    private final CommandLineParser parser = new DefaultParser();
    private final Options options = new Options();

    public ArgumentParser() {
        options.addOption("o", "outputFolder", true, "output folder, current folder if not specified");
    }

    public List<Transaction> parse(final String... args) {

        try {
            final CommandLine line = parser.parse(options, args);
            System.out.println(line.getArgList());
            return line.getArgList().stream().map(Transaction::new).collect(Collectors.toList());


        } catch (final ParseException e) {
            System.out.println(e.getMessage());
            final HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("java -jar capture-em-all.jar", options);

            return Collections.emptyList();
        }
    }
}
