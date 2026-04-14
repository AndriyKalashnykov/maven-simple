package jsonparse.pathqueries;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonPathExampleTest {

  protected static final Logger LOGGER = LoggerFactory.getLogger(JsonPathExampleTest.class);

  @Test
  public void mainTest() throws IOException {

    JsonPathExample.main(new String[] {});
  }
}
