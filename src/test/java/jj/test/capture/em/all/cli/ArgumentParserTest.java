package jj.test.capture.em.all.cli;

import jj.test.capture.em.all.core.Transaction;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Test
public class ArgumentParserTest {

    public void test_parse() {
        final ArgumentParser parser = new ArgumentParser(System.out);
        final Transaction[] expectedTransactions = new Transaction[]{
                new Transaction("file://bla/bla.txt"),
                new Transaction("https://another/url"),
                new Transaction("you-shall-not-pass")
        };

        assertThat(
                parser.parse(new String[]{"file://bla/bla.txt", "https://another/url", "you-shall-not-pass"})
        ).containsOnly(expectedTransactions);
    }

    public void test_parseEmpty() {
        final ArgumentParser parser = new ArgumentParser(System.out);

        assertThat(parser.parse("")).isEmpty();
    }

    public void test_invalidOption() {
        final ArgumentParser parser = new ArgumentParser(System.out);

        assertThat(parser.parse("-unexpected-journey")).isEmpty();
    }

    public void test_validOption_noTransactions() {
        final ArgumentParser parser = new ArgumentParser(System.out);

        assertThat(parser.parse("-o=out")).isEmpty();
    }
}
