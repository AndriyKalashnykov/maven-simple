package jsonparse.treemodels;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JacksonTreeModelTest {

  protected static final Logger LOGGER = LoggerFactory.getLogger(JacksonTreeModelTest.class);

  @Test
  public void mainTest() throws IOException {

    JacksonTreeModel.main(new String[] {});
  }
}
