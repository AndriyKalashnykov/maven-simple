package jsonparse.treemodels;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class JacksonTreeModelTest {

    protected static final Logger LOGGER = LoggerFactory.getLogger(JacksonTreeModelTest.class);

    @Test
    public void mainTest() throws IOException {

        JacksonTreeModel.main(new String[]{});
    }
}
