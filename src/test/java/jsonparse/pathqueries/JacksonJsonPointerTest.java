package jsonparse.pathqueries;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JacksonJsonPointerTest {

  protected static final Logger LOGGER = LoggerFactory.getLogger(JacksonJsonPointerTest.class);

  @Test
  public void mainTest() throws IOException {

    JacksonJsonPointer.main(new String[] {});
  }
}
