package jj.test.capture.em.all.core;

import jj.test.capture.em.all.protocol.ApacheCommonsIO;
import jj.test.capture.em.all.protocol.KnownProtocol;
import jj.test.capture.em.all.protocol.Sftp;

import java.io.Closeable;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class TransferManager implements Closeable {
    private final PrintStream printer;
    private final ExecutorService executorService;
    private final List<KnownProtocol> knownProtocols;

    private final List<Future<Transfer>> transfers = new ArrayList<>();

    /**
     * Starts a TransactionManager with 10 max concurrent threads.
     */
    public TransferManager(final PrintStream printer) {
        this(printer, 10);
    }

    public TransferManager(final PrintStream printer, final int concurrentThreads) {
        this(
                printer,
                concurrentThreads,
                Arrays.asList(
                        new Sftp(),
                        new ApacheCommonsIO()
                )
        );
    }

    public TransferManager(final PrintStream printer, final int concurrentThreads, final List<KnownProtocol> knownProtocols) {
        this.printer = printer;
        this.knownProtocols = knownProtocols;
        executorService = Executors.newWorkStealingPool(concurrentThreads);
    }

    public Optional<KnownProtocol> getKnownProtocol(final String protocol) {
        return knownProtocols
                .stream()
                .filter(knownProtocol -> knownProtocol.getKnownProtocols().stream().anyMatch(protocol::equalsIgnoreCase))
                .findFirst();
    }

    public void captureEmAll(final List<Transaction> transactions) {
        extractCandidates(transactions)
                .forEach(transaction ->
                        getKnownProtocol(transaction.getProtocol())
                                .ifPresent(
                                        knownProtocol -> transfers.add(
                                                executorService.submit(() -> {
                                                    final Transfer transfer = knownProtocol.transfer(transaction);
                                                    printFinished(transfer);
                                                    return transfer;
                                                })
                                        )
                                )
                );
    }

    public void printFinished(final Transfer transfer) {
        printer.format("Complete: %s\n", transfer.getFeedback());
    }

    protected List<Transaction> extractCandidates(final List<Transaction> transactions) {
        final List<Transaction> invalidTransactions = getInvalidSources(transactions);
        final List<Transaction> unknownProtocols = getUnknownProtocols(
                transactions.stream().filter(transaction -> !invalidTransactions.contains(transaction)).collect(Collectors.toList())
        );

        print(invalidTransactions, "Invalid source=%s\n");
        print(unknownProtocols, "Unknown protocol=%s\n");

        return transactions
                .stream()
                .filter(transaction -> (!invalidTransactions.contains(transaction) && !unknownProtocols.contains(transaction)))
                .collect(Collectors.toList());
    }

    private void print(final List<Transaction> transactions, final String message) {
        transactions.forEach(transaction -> printer.format(message, transaction.getSource()));
    }

    protected List<Transaction> getInvalidSources(final List<Transaction> transactions) {
        return transactions.stream().filter(Transaction::isNotValid).collect(Collectors.toList());
    }

    protected List<Transaction> getUnknownProtocols(final List<Transaction> transactions) {
        return transactions
                .stream()
                .filter(transaction -> !getKnownProtocol(transaction.getProtocol()).isPresent())
                .collect(Collectors.toList());
    }

    public boolean isDone() {
        return transfers.stream().allMatch(Future::isDone);
    }

    @Override
    public void close() {
        executorService.shutdown();
    }

    public List<Transfer> getResults() {
        return transfers.stream().map(transferFuture -> {
            try {
                return transferFuture.get();
            } catch (final InterruptedException | ExecutionException e) {
                return new Transfer(-1, -1, Transfer.Status.ERROR, e.getMessage());
            }
        }).collect(Collectors.toList());
    }
}
