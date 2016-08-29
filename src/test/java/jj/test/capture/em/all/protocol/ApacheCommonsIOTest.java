package jj.test.capture.em.all.protocol;

import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Test
public class ApacheCommonsIOTest {

    public void test_knownProtocols() {
        assertThat(new ApacheCommonsIO().getKnownProtocols()).containsOnly("http", "https", "ftp", "file");
    }
}
