package http.client.retrofit;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

public class RetrofitDemoTest {

    protected static final Logger LOGGER = LoggerFactory.getLogger(RetrofitDemoTest.class);

    @Test
    public void mainTest() throws ExecutionException, InterruptedException {

        RetrofitDemo.main(new String[]{});
    }
}
