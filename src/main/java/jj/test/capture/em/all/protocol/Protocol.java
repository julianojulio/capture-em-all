package jj.test.capture.em.all.protocol;

import jj.test.capture.em.all.core.Transaction;

import java.util.List;

public interface Protocol {
    List<String> getKnownProtocols();
    Transfer newTransfer(final Transaction transaction);
}
