package http.client.apache;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ApacheHttpClientUserDemoTest {

    protected static final Logger LOGGER = LoggerFactory.getLogger(ApacheHttpClientUserDemoTest.class);

    @Test
    public void mainTest() throws IOException {

        ApacheHttpClientUserDemo.main(new String[]{});
    }
}
