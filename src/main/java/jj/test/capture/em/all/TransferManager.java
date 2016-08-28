package jj.test.capture.em.all;

import com.google.common.collect.ImmutableMap;
import jj.test.capture.em.all.protocol.Sftp;

import java.io.Closeable;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class TransferManager implements Closeable {
    private final PrintStream printer;
    private final int concurrentThreads;
    private final ExecutorService executorService;
    private static final ImmutableMap<String, Sftp> TRANSFER_HANDLERS = ImmutableMap.of("sftp", new Sftp());

    private final List<Future<Transfer>> transfers = new ArrayList<>();

    /**
     * Starts a TransactionManager with 10 max concurrent threads.
     */
    public TransferManager(final PrintStream printer) {
        this(printer, 10);
    }

    public TransferManager(final PrintStream printer, final int concurrentThreads) {
        this.printer = printer;
        this.concurrentThreads = concurrentThreads;

        executorService = Executors.newFixedThreadPool(10);
    }

    public void captureEmAll(final List<Transaction> transactions) {
        extractCandidates(transactions)
                .forEach(transaction -> {
                    if (TRANSFER_HANDLERS.containsKey(transaction.getProtocol())) {
                        transfers.add(
                                executorService.submit(() -> TRANSFER_HANDLERS.get(transaction.getProtocol()).transfer(transaction))
                        );
                    }
                });
    }

    protected List<Transaction> extractCandidates(final List<Transaction> transactions) {
        final List<Transaction> invalidTransactions = filterInvalidSources(transactions);
        final List<Transaction> invalidProtocols = filterUnknownProtocols(
                transactions.stream().filter(transaction -> !invalidTransactions.contains(transaction)).collect(Collectors.toList())
        );

        print(invalidTransactions, "Invalid source=%s\n");
        print(invalidProtocols, "Unknown protocol=%s\n");

        return transactions
                .stream()
                .filter(transaction -> !invalidTransactions.contains(transaction) || !invalidProtocols.contains(transaction))
                .collect(Collectors.toList());
    }

    private void print(final List<Transaction> transactions, final String message) {
        transactions.forEach(transaction -> printer.format(message, transaction.getSource()));
    }

    private List<Transaction> filterInvalidSources(final List<Transaction> transactions) {
        return transactions.stream().filter(Transaction::isNotValid).collect(Collectors.toList());
    }

    private List<Transaction> filterUnknownProtocols(final List<Transaction> transactions) {
        return transactions
                .stream()
                .filter(transaction -> !TRANSFER_HANDLERS.containsKey(transaction.getProtocol()))
                .collect(Collectors.toList());
    }

    public boolean isDone() {
        return transfers.stream().allMatch(Future::isDone);
    }

    @Override
    public void close() {
        executorService.shutdown();
    }
}
