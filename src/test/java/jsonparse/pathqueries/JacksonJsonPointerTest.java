package jsonparse.pathqueries;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JacksonJsonPointerTest {

  @Test
  @DisplayName("Jackson JsonPointer /element_count resolves the NEO count from source.json")
  void resolvesNeoCountViaPointer() throws Exception {
    String out = captureMain();
    assertTrue(out.contains("NEO count: 101"), out);
  }

  private static String captureMain() throws Exception {
    PrintStream original = System.out;
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));
    try {
      JacksonJsonPointer.main(new String[] {});
    } finally {
      System.setOut(original);
    }
    return buffer.toString(StandardCharsets.UTF_8);
  }
}
