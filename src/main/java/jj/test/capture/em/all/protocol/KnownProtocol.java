package jj.test.capture.em.all.protocol;

import jj.test.capture.em.all.core.Transaction;
import jj.test.capture.em.all.core.Transfer;

import java.util.List;

public interface KnownProtocol {
    List<String> getKnownProtocols();

    Transfer transfer(final Transaction transaction);
}
