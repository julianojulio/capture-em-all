package jj.test.capture.em.all.core;

import jj.test.capture.em.all.protocol.ApacheCommonsIO;
import jj.test.capture.em.all.protocol.Protocol;
import jj.test.capture.em.all.protocol.Sftp;
import jj.test.capture.em.all.protocol.Transfer;

import java.io.Closeable;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class TransferManager implements Closeable {
    private final PrintStream printer;
    private final ExecutorService executorService;
    private final List<Protocol> protocols;

    private final List<Future<TransferStatus>> transfers = new ArrayList<>();

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

    public TransferManager(final PrintStream printer, final int concurrentThreads, final List<Protocol> protocols) {
        this.printer = printer;
        this.protocols = protocols;
        executorService = Executors.newWorkStealingPool(concurrentThreads);
    }

    public Optional<Protocol> getKnownProtocol(final String protocol) {
        return protocols
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


                                                    final Transfer transfer = knownProtocol.newTransfer(transaction);
                                                    final TransferStatus transferStatus = transfer.start();
                                                    printFinished(transferStatus);
                                                    return transferStatus;
                                                })
                                        )
                                )
                );
    }

    public void printFinished(final TransferStatus transferStatus) {
        printer.format("Complete: %s\n", transferStatus.getFeedback());
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
}
