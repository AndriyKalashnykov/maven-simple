package jsonparse.databinding.compex.gson;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import jsonparse.databinding.complex.gson.ComplexDataBindingGson;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ComplexDataBindingGsonTest {

  @Test
  @DisplayName("Gson full-schema binding reports NEO count, hazardous count, and fastest NEO")
  void bindsFullSchemaAndComputesAggregates() throws Exception {
    String out = captureMain();
    assertTrue(out.contains("NEO count: 101"), out);
    assertTrue(out.contains("Potentially hazardous asteroids: 19"), out);
    assertTrue(out.contains("Fastest NEO is: 526898 (2007 HR)"), out);
  }

  private static String captureMain() throws Exception {
    PrintStream original = System.out;
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));
    try {
      ComplexDataBindingGson.main(new String[] {});
    } finally {
      System.setOut(original);
    }
    return buffer.toString(StandardCharsets.UTF_8);
  }
}
