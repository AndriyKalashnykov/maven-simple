package http.client.java;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class JavaHttpClientDemoTest {

    protected static final Logger LOGGER = LoggerFactory.getLogger(JavaHttpClientDemoTest.class);

    @Test
    public void mainTest() throws IOException, ExecutionException, InterruptedException {

        JavaHttpClientDemo.main(new String[]{});
    }
}
