package jj.test.capture.em.all.protocol;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import jj.test.capture.em.all.core.Transaction;
import jj.test.capture.em.all.core.Transfer;
import org.apache.commons.io.IOUtils;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

public class Sftp implements KnownProtocol {

    public Transfer transfer(final Transaction transaction) {
        final JSch jsch = new JSch();

        try {
            final Session session = jsch.getSession(transaction.getUsername(), transaction.getHost(), transaction.getPort(22));
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(transaction.getPassword());
            session.connect();

            final Channel channel = session.openChannel("sftp");
            channel.connect();

            final ChannelSftp sftpChannel = (ChannelSftp) channel;
            final long size = sftpChannel.lstat(transaction.getPath()).getSize();

            Files.createDirectories(transaction.getOutputPath());

            final InputStream inputStream = sftpChannel.get(transaction.getPath());
            final long copiedBytes = IOUtils.copyLarge(
                    inputStream,
                    new BufferedOutputStream(new FileOutputStream(transaction.getDestination()))
            );
            inputStream.close();

            sftpChannel.exit();
            session.disconnect();

            return new Transfer(size, copiedBytes, Transfer.Status.FINISHED);
        } catch (final JSchException | SftpException | IOException e) {
            return new Transfer(-1, -1, Transfer.Status.ERROR, e.getMessage());
        }
    }

    @Override
    public List<String> getKnownProtocols() {
        return Collections.singletonList("sftp");
    }
}
