package http.client.java;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class JavaHttpURLConnectionDemoTest {

    protected static final Logger LOGGER = LoggerFactory.getLogger(JavaHttpURLConnectionDemoTest.class);

    @Test
    public void mainTest() throws IOException {

        JavaHttpURLConnectionDemo.main(new String[]{});
    }
}
