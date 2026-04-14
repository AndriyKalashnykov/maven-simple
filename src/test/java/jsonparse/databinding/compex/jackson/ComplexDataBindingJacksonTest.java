package jsonparse.databinding.compex.jackson;

import jsonparse.databinding.complex.jackson.ComplexDataBindingJackson;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComplexDataBindingJacksonTest {

  protected static final Logger LOGGER =
      LoggerFactory.getLogger(ComplexDataBindingJacksonTest.class);

  @Test
  public void mainTest() {

    ComplexDataBindingJackson.main(new String[] {});
  }
}
