package jsonparse.pathqueries;

import jsonparse.SourceData;
import tools.jackson.core.JsonPointer;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

public class JacksonJsonPointer {

  public static void main(String[] args) {

    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode node = objectMapper.readTree(SourceData.asString());

    JsonPointer pointer = JsonPointer.compile("/element_count");

    System.out.println("NEO count: " + node.at(pointer).asString());
  }
}
