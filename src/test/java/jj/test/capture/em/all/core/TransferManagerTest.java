package jj.test.capture.em.all.core;

import com.google.common.collect.ImmutableList;
import jj.test.capture.em.all.protocol.Protocol;
import jj.test.capture.em.all.protocol.Transfer;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Test
public class TransferManagerTest {
    private final Protocol httpFailTransfer = new Protocol() {
        @Override
        public List<String> getKnownProtocols() {
            return Collections.singletonList("http");
        }

        @Override
        public Transfer newTransfer(final Transaction transaction) {
            throw new RuntimeException("Not implemented - unit test mock");
        }
    };

    public void test_emptyForUnknownProtocol() {
        final TransferManager transferManager = new TransferManager(System.out, 1, ImmutableList.of(httpFailTransfer));
        assertThat(transferManager.getKnownProtocol("file")).isEmpty();
    }

    public void test_knownProtocol() {
        final TransferManager transferManager = new TransferManager(System.out, 1, ImmutableList.of(httpFailTransfer));
        assertThat(transferManager.getKnownProtocol("http")).contains(httpFailTransfer);
    }

    public void test_filterUnknownProtocol() {
        final TransferManager transferManager = new TransferManager(System.out, 1, ImmutableList.of(httpFailTransfer));
        final List<Transaction> transactions = Arrays.asList(
                new Transaction("file://bla/bla.txt"),
                new Transaction("http://another/url")
        );

        assertThat(transferManager.getUnknownProtocols(transactions)).containsOnly(new Transaction("file://bla/bla.txt"));
    }

    public void test_filterUnknownProtocol_Empty() {
        final TransferManager transferManager = new TransferManager(System.out, 1, ImmutableList.of(httpFailTransfer));
        final List<Transaction> transactions = Arrays.asList(
                new Transaction("http://bla/bla.txt"),
                new Transaction("http://another/url")
        );

        assertThat(transferManager.getUnknownProtocols(transactions)).isEmpty();
    }

    public void test_filterInvalidSources_Empty() {
        final TransferManager transferManager = new TransferManager(System.out, 1, ImmutableList.of(httpFailTransfer));
        final List<Transaction> transactions = Arrays.asList(
                new Transaction("file://bla/bla.txt"),
                new Transaction("https://another/url")
        );

        assertThat(transferManager.getInvalidSources(transactions)).isEmpty();
    }

    public void test_filterInvalidSources() {
        final TransferManager transferManager = new TransferManager(System.out, 1, ImmutableList.of(httpFailTransfer));
        final List<Transaction> transactions = Arrays.asList(
                new Transaction("file://bla/bla.txt"),
                new Transaction("https://another/url"),
                new Transaction("you-shall-not-pass")
        );

        assertThat(transferManager.getInvalidSources(transactions)).containsOnly(new Transaction("you-shall-not-pass"));
    }


    public void test_extractCandidates() {
        final TransferManager transferManager = new TransferManager(System.out, 1, ImmutableList.of(httpFailTransfer));
        final List<Transaction> transactions = Arrays.asList(
                new Transaction("file://bla/bla.txt"),
                new Transaction("https://another/url"),
                new Transaction("you-shall-not-pass"),
                new Transaction("http://media.giphy.com/media/12z0zQF2qdtTz2/giphy.gif")
        );

        assertThat(transferManager.extractCandidates(transactions)).containsOnly(new Transaction("http://media.giphy.com/media/12z0zQF2qdtTz2/giphy.gif"));
    }
}
