package jj.test.capture.em.all;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;

public class Transaction {

    private final String source;

    public Transaction(final String source) {
        this.source = source;
    }

    public String getDestination() {
        return FilenameUtils.getName(source);
    }

    public URI getUri() {
        try {
            return new URI(source);
        } catch (final URISyntaxException e) {
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

    public boolean isValid() {
        return StringUtils.isNotBlank(getUri().getScheme());
    }

    public boolean isNotValid() {
        return !isValid();
    }
}
