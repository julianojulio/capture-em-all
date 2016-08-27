package jj.test.capture.em.all;

public class Transfer {

    public static enum Status {
        IN_PROGRESS,
        FINISHED,
        CANCELED,
        ERROR
    }

    final long size;
    final long copied;
    final Status status;
    final String feedback;

    public Transfer(final long size, final long copied, final Status status, final String feedback) {
        this.size = size;
        this.copied = copied;
        this.status = status;
        this.feedback = feedback;
    }

    public Transfer(final long size, final long copied, final Status status) {
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
