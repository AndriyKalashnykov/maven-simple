package jsonparse.databinding.compex.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import jsonparse.databinding.complex.jackson.ComplexDataBindingJackson;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComplexDataBindingJacksonTest {

    protected static final Logger LOGGER = LoggerFactory.getLogger(ComplexDataBindingJacksonTest.class);

    @Test
    public void mainTest() throws JsonProcessingException {

        ComplexDataBindingJackson.main(new String[]{});
    }
}
