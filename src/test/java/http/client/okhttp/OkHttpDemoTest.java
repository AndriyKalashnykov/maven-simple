package http.client.okhttp;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OkHttpDemoTest {

  protected static final Logger LOGGER = LoggerFactory.getLogger(OkHttpDemoTest.class);

  @Test
  public void mainTest() throws IOException {

    OkHttpDemo.main(new String[] {});
  }
}
