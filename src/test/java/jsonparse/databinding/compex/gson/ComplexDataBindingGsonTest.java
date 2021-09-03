package jsonparse.databinding.compex.gson;

import jsonparse.databinding.complex.gson.ComplexDataBindingGson;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComplexDataBindingGsonTest {

    protected static final Logger LOGGER = LoggerFactory.getLogger(ComplexDataBindingGsonTest.class);

    @Test
    public void mainTest() {

        ComplexDataBindingGson.main(new String[]{});
    }
}
