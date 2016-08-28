package jj.test.capture.em.all;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class Transaction {

    private final String source;
    private final String outputFolder;

    public Transaction(final String source) {
        this(source, "download");
    }

    public Transaction(final String source, final String outputFolder) {
        this.source = source;
        this.outputFolder = outputFolder;
    }

    public String getOutputFolder() {
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

    public String getUsername() {
        try {
            return getUri().getUserInfo().split(":")[0];
        } catch (final Exception e) {
            return "";
        }
    }

    public String getPassword() {
        try {
            return getUri().getUserInfo().split(":")[1];
        } catch (final Exception e) {
            return "";
        }
    }

    public String getHost() {
        return getUri().getHost();
    }

    public int getPort() {
        return getUri().getPort();
    }

    /**
     * Returns the Port number, if not specified, then returns the given parameter.
     */
    public int getPort(final int defaultIfNotSpecified) {
        return getPort() > 0 ? getPort() : defaultIfNotSpecified;
    }

    public Path getOutputPath() {
        return Paths.get(outputFolder);
    }

    public File getDestination() {
        return getOutputPath().resolve(getFilename()).toFile();
    }

    public String getFilename() {
        return FilenameUtils.getName(source);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (!(other instanceof Transaction)) return false;
        final Transaction that = (Transaction) other;
        return Objects.equals(source, that.source) &&
                Objects.equals(outputFolder, that.outputFolder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, outputFolder);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "source='" + source + '\'' +
                ", outputFolder='" + outputFolder + '\'' +
                '}';
    }
}
