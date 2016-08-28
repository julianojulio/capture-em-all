package jj.test.capture.em.all.protocol;

import jj.test.capture.em.all.Transaction;
import jj.test.capture.em.all.Transfer;

import java.util.List;

public interface KnownProtocol {
    List<String> getKnownProtocols();

    Transfer transfer(final Transaction transaction);
}
