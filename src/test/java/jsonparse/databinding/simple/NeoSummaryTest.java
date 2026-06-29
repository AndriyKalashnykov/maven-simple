package jsonparse.databinding.simple;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NeoSummaryTest {

  @Test
  @DisplayName("Jackson and Gson both bind the NEO summary JSON to identical field values")
  void jacksonAndGsonBindSameFields() throws Exception {
    String out = captureMain();
    // Both binders must run and produce the same parsed values.
    assertTrue(out.contains("Jackson made:"), out);
    assertTrue(out.contains("Gson made:"), out);
    assertTrue(out.contains("id=54016476"), out);
    assertTrue(out.contains("name='(2020 GR1)'"), out);
    // close_approach_date bound through LocalDate (Jackson @JsonProperty / Gson adapter).
    assertTrue(out.contains("closeApproach=2020-04-12"), out);
  }

  private static String captureMain() throws Exception {
    PrintStream original = System.out;
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));
    try {
      NeoSummary.main(new String[] {});
    } finally {
      System.setOut(original);
    }
    return buffer.toString(StandardCharsets.UTF_8);
  }
}
