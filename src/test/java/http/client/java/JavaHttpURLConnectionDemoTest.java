package http.client.java;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaHttpURLConnectionDemoTest {

  protected static final Logger LOGGER =
      LoggerFactory.getLogger(JavaHttpURLConnectionDemoTest.class);

  @Test
  public void mainTest() throws IOException {

    JavaHttpURLConnectionDemo.main(new String[] {});
  }
}
