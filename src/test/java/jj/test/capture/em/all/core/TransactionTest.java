package jj.test.capture.em.all.core;

import jj.test.capture.em.all.core.Transaction;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Test
public class TransactionTest {

    public void test_loginData() {
        final Transaction transaction = new Transaction("sftp://demo:password@test.rebex.net/readme.txt");
        assertThat(transaction.getUsername()).isEqualTo("demo");
        assertThat(transaction.getPassword()).isEqualTo("password");
    }

    public void test_loginData_noPassword() {
        final Transaction transaction = new Transaction("sftp://demo@test.rebex.net/readme.txt");
        assertThat(transaction.getUsername()).isEqualTo("demo");
        assertThat(transaction.getPassword()).isEqualTo("");
    }

    public void test_loginData_noUsername() {
        final Transaction transaction = new Transaction("sftp://:password@test.rebex.net/readme.txt");
        assertThat(transaction.getUsername()).isEqualTo("");
        assertThat(transaction.getPassword()).isEqualTo("password");
    }

    public void test_loginData_noData() {
        final Transaction transaction = new Transaction("sftp://test.rebex.net/readme.txt");
        assertThat(transaction.getUsername()).isEqualTo("");
        assertThat(transaction.getPassword()).isEqualTo("");
    }

    public void test_getHost() {
        final Transaction transaction = new Transaction("sftp://test.rebex.net/readme.txt");
        assertThat(transaction.getHost()).isEqualTo("test.rebex.net");
    }

    public void test_getPort() {
        final Transaction transaction = new Transaction("sftp://test.rebex.net/readme.txt");
        assertThat(transaction.getPort()).isEqualTo(-1);
        assertThat(transaction.getPort(22)).isEqualTo(22);
    }
}
