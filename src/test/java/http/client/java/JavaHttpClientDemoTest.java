package http.client.java;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaHttpClientDemoTest {

  protected static final Logger LOGGER = LoggerFactory.getLogger(JavaHttpClientDemoTest.class);

  @Test
  public void mainTest() throws IOException, ExecutionException, InterruptedException {

    JavaHttpClientDemo.main(new String[] {});
  }
}
