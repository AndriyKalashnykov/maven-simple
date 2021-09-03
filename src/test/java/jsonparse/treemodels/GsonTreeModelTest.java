package jsonparse.treemodels;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class GsonTreeModelTest {

    protected static final Logger LOGGER = LoggerFactory.getLogger(GsonTreeModelTest.class);

    @Test
    public void mainTest() throws IOException {

        GsonTreeModel.main(new String[]{});
    }
}
