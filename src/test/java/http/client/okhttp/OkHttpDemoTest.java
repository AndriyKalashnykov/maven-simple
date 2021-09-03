package http.client.okhttp;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class OkHttpDemoTest {

    protected static final Logger LOGGER = LoggerFactory.getLogger(OkHttpDemoTest.class);

    @Test
    public void mainTest() throws IOException {

        OkHttpDemo.main(new String[]{});
    }
}
