package jsonparse.pathqueries;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class JsonPathExampleTest {

    protected static final Logger LOGGER = LoggerFactory.getLogger(JsonPathExampleTest.class);

    @Test
    public void mainTest() throws IOException {

        JsonPathExample.main(new String[]{});
    }
}
