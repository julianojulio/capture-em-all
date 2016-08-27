package jj.test.capture.em.all;

import org.apache.commons.io.FilenameUtils;

import java.net.URI;
import java.net.URISyntaxException;

public class Transaction {

    final String source;

    public Transaction(final String source) {
        this.source = source;
    }

    public String getDestination() {
        return FilenameUtils.getName(source);
    }

    public URI getUri() {
        try {
            return new URI(source);
        } catch (URISyntaxException e) {
            throw new TransactionException("Can't create an URI from source=" + source, e);
        }
    }

    public String getProtocol() {
        return getUri().getScheme();
    }

    public String getSource() {
        return source;
    }

    public String getPath() {
        return getUri().getPath();
    }
}
