package jsonparse.treemodels;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GsonTreeModelTest {

  protected static final Logger LOGGER = LoggerFactory.getLogger(GsonTreeModelTest.class);

  @Test
  public void mainTest() throws IOException {

    GsonTreeModel.main(new String[] {});
  }
}
