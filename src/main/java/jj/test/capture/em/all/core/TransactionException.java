package jj.test.capture.em.all.core;

public class TransactionException extends RuntimeException {
    public TransactionException(final String message) {
        super(message);
    }

    public TransactionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
