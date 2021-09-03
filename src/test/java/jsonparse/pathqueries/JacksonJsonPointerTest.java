package jsonparse.pathqueries;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class JacksonJsonPointerTest {

    protected static final Logger LOGGER = LoggerFactory.getLogger(JacksonJsonPointerTest.class);

    @Test
    public void mainTest() throws IOException {

        JacksonJsonPointer.main(new String[]{});
    }
}
