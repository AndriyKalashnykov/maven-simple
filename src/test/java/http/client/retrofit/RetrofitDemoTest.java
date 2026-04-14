package http.client.retrofit;

import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RetrofitDemoTest {

  protected static final Logger LOGGER = LoggerFactory.getLogger(RetrofitDemoTest.class);

  @Test
  public void mainTest() throws ExecutionException, InterruptedException {

    RetrofitDemo.main(new String[] {});
  }
}
