package http.client.retrofit;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;


public class RetrofitCustomClientDemoTest {

    protected static final Logger LOGGER = LoggerFactory.getLogger(RetrofitCustomClientDemoTest.class);

    @Test
    public void mainTest() throws ExecutionException, InterruptedException {

        RetrofitCustomClientDemo.main(new String[]{});
    }
}
