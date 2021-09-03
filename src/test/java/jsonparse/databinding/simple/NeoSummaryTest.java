package jsonparse.databinding.simple;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class NeoSummaryTest {

    protected static final Logger LOGGER = LoggerFactory.getLogger(NeoSummaryTest.class);

    @Test
    public void mainTest() throws IOException {

        NeoSummary.main(new String[]{});
    }
}
