package jsonparse;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SourceData {

    public static final String SOURCE_JSON = "source.json";

    public static String asString() {
        try {
            Stream<String> lines = Files.lines(
                    Paths.get(ClassLoader.getSystemResource(SOURCE_JSON).toURI()));

            return lines.collect(Collectors.joining());

        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}
