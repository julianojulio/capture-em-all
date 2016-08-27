package jj.test.capture.em.all;

public class Transference {

    final String source;
    final String destination;
    final String protocol;
    final Transfer transfer;

    public Transference(final String source, final String destination, final String protocol, final Transfer transfer) {
        this.source = source;
        this.destination = destination;
        this.protocol = protocol;
        this.transfer = transfer;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder()
                .withSource(source)
                .withDestination(destination)
                .withProtocol(protocol)
                .withTransfer(transfer);
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public String getProtocol() {
        return protocol;
    }

    public Transfer getTransfer() {
        return transfer;
    }

    public static class Builder {


        private String source;
        private String destination;
        private String protocol;
        private Transfer transfer;

        public Builder withSource(final String source) {
            this.source = source;
            return this;
        }

        public Builder withDestination(final String destination) {
            this.destination = destination;
            return this;
        }

        public Builder withProtocol(final String protocol) {
            this.protocol = protocol;
            return this;
        }

        public Builder withTransfer(final Transfer transfer) {
            this.transfer = transfer;
            return this;
        }

        public Transference build() {
            return new Transference(source, destination, protocol, transfer);
        }
    }
}
