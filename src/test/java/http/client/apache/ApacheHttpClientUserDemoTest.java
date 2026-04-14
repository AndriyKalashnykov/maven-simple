package http.client.apache;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApacheHttpClientUserDemoTest {

  protected static final Logger LOGGER =
      LoggerFactory.getLogger(ApacheHttpClientUserDemoTest.class);

  @Test
  public void mainTest() throws IOException {

    ApacheHttpClientUserDemo.main(new String[] {});
  }
}
