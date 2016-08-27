package jj.test.capture.em.all;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TransferManager {
    private final PrintStream printer;

    public TransferManager(final PrintStream printer) {
        this.printer = printer;
    }

    public void captureEmAll(final List<Transaction> transactions) {
        final List<Transaction> invalidTransactions = filterInvalidTransactions(transactions);
        printInvalidTransactions(invalidTransactions);
    }

    private void printInvalidTransactions(final List<Transaction> invalidTransactions) {
        System.out.println(Arrays.toString(invalidTransactions.toArray()));
        invalidTransactions.forEach(transaction -> {
            printer.format("Invalid source=%s\n", transaction.getSource());
        });
    }

    private List<Transaction> filterInvalidTransactions(final List<Transaction> transactions) {
        return transactions.stream().filter(Transaction::isNotValid).collect(Collectors.toList());
    }

    public boolean inProgress() {
        return false;
    }
}
