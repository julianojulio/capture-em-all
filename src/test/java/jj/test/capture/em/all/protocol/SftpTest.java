package jj.test.capture.em.all.protocol;

import com.google.common.collect.ImmutableMap;
import jj.test.capture.em.all.Transaction;
import jj.test.capture.em.all.Transfer;
import org.apache.sshd.SshServer;
import org.apache.sshd.common.Session;
import org.apache.sshd.common.file.FileSystemView;
import org.apache.sshd.common.file.nativefs.NativeFileSystemFactory;
import org.apache.sshd.common.file.nativefs.NativeFileSystemView;
import org.apache.sshd.server.command.ScpCommandFactory;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.sftp.subsystem.SftpSubsystem;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@Test
public class SftpTest {

    public static final String USERNAME = "login";
    public static final String PASSWORD = "password";
    private SshServer sshd;
    private final File outputFolder = new File(getClass().getResource("/").getPath());

    @BeforeMethod
    private void startSftpServer() throws IOException {
        sshd = SshServer.setUpDefaultServer();
        sshd.setPort(22000);
        sshd.setPasswordAuthenticator((username, password, session) -> (USERNAME.equals(username) && PASSWORD.equals(password)));
        sshd.setPublickeyAuthenticator((username, key, session) -> false);
        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());
        sshd.setSubsystemFactories(Collections.singletonList(new SftpSubsystem.Factory()));
        sshd.setCommandFactory(new ScpCommandFactory());

        sshd.setFileSystemFactory(new NativeFileSystemFactory() {
            @Override
            public FileSystemView createFileSystemView(final Session session) {
                final boolean caseInsensitive = false;
                final char folderSeparator = '/';
                final String currentFolder = "/";
                return new NativeFileSystemView(
                        USERNAME,
                        ImmutableMap.of("/", outputFolder.getPath()),
                        currentFolder,
                        folderSeparator,
                        caseInsensitive
                );
            }
        });

        sshd.start();
    }

    @AfterMethod
    private void stopSftpServer() throws InterruptedException {
        sshd.stop();
    }

    public void test_download() {
        final Transfer transfer = new Sftp().transfer(new Transaction("sftp://login:password@localhost:22000/true-story.png"));

        assertThat(transfer).isEqualToComparingFieldByField(
                new Transfer(130093L, 130093L, Transfer.Status.FINISHED)
        );
    }

}