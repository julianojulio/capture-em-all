package jj.test.capture.em.all.core;

public class TransferStatus {

    public static enum Status {
        FINISHED,
        ERROR
    }

    final long size;
    final long copied;
    final Status status;
    final String feedback;

    public TransferStatus(final long size, final long copied, final Status status, final String feedback) {
        this.size = size;
        this.copied = copied;
        this.status = status;
        this.feedback = feedback;
    }

    public TransferStatus(final long size, final long copied, final Status status) {
        this(size, copied, status, "");
    }

    public long getSize() {
        return size;
    }

    public long getCopied() {
        return copied;
    }

    public Status getStatus() {
        return status;
    }

    public String getFeedback() {
        return feedback;
    }

    public boolean isComplete() {
        return size == copied;
    }
}
