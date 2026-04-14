package jsonparse.databinding.simple;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NeoSummaryTest {

  protected static final Logger LOGGER = LoggerFactory.getLogger(NeoSummaryTest.class);

  @Test
  public void mainTest() throws IOException {

    NeoSummary.main(new String[] {});
  }
}
