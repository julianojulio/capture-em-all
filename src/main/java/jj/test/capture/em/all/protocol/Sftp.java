package jj.test.capture.em.all.protocol;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import jj.test.capture.em.all.Transaction;
import jj.test.capture.em.all.Transfer;
import org.apache.commons.io.IOUtils;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Sftp {

    public Transfer transfer(final Transaction transaction) {
        final JSch jsch = new JSch();

        try {
            final Session session = jsch.getSession("username", "127.0.0.1", 22);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword("password");
            session.connect();

            final Channel channel = session.openChannel("sftp");
            channel.connect();

            final ChannelSftp sftpChannel = (ChannelSftp) channel;
            final InputStream inputStream = sftpChannel.get(transaction.getSource());

            final long size = sftpChannel.lstat(transaction.getPath()).getSize();
            final long copiedBytes = IOUtils.copyLarge(
                    inputStream,
                    new BufferedOutputStream(new FileOutputStream(transaction.getDestination()))
            );

            sftpChannel.exit();
            session.disconnect();

            return new Transfer(size, copiedBytes, Transfer.Status.FINISHED);
        } catch (final JSchException | SftpException | IOException e) {
            return new Transfer(-1, -1, Transfer.Status.ERROR, e.getMessage());
        }
    }
}
