package jj.test.capture.em.all.protocol;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import jj.test.capture.em.all.core.Transaction;
import jj.test.capture.em.all.core.TransactionException;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

public class Sftp implements Protocol {

    public SftpTransfer newTransfer(final Transaction transaction) {
        return new SftpTransfer(transaction);
    }

    @Override
    public List<String> getKnownProtocols() {
        return Collections.singletonList("sftp");
    }

    public static class SftpTransfer extends BaseTransfer {
        private final JSch jsch = new JSch();
        private final ChannelSftp sftpChannel;
        private final Session session;

        public SftpTransfer(final Transaction transaction) {
            super(transaction);

            try {
                session = jsch.getSession(transaction.getUsername(), transaction.getHost(), transaction.getPort(22));
                session.setConfig("StrictHostKeyChecking", "no");
                session.setPassword(transaction.getPassword());
                session.connect();

                final Channel channel = session.openChannel("sftp");
                channel.connect();

                sftpChannel = (ChannelSftp) channel;
            } catch (final JSchException e) {
                throw new TransactionException("Error transferring: " + transaction.getSource(), e);
            }
        }

        @Override
        InputStream openInputStream(final String path) {
            try {
                return sftpChannel.get(transaction.getPath());
            } catch (final SftpException e) {
                throw new TransactionException("Error connection to: " + transaction.getSource(), e);
            }
        }

        @Override
        long getSize() {
            try {
                return sftpChannel.lstat(transaction.getPath()).getSize();
            } catch (final SftpException e) {
                throw new TransactionException("Error retrieving size of: " + transaction.getSource(), e);
            }
        }

        @Override
        void close() {
            sftpChannel.exit();
            session.disconnect();
        }
    }
}
