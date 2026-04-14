package http.client.java;

import http.client.model.Page;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.ObjectMapper;

public class JavaHttpURLConnectionDemo {

  protected static final Logger LOGGER = LoggerFactory.getLogger(JavaHttpURLConnectionDemo.class);

  public static void main(String[] args) throws IOException {

    // Create a neat value object to hold the URL
    URL url = URI.create("https://jsonmock.hackerrank.com/api/article_users?page=2").toURL();

    // Open a connection(?) on the URL(?) and cast the response(??)
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

    // Now it's "open", we can set the request method, headers etc.
    connection.setRequestProperty("accept", "application/json");

    // This line makes the request
    InputStream responseStream = connection.getInputStream();

    // Manually converting the response body InputStream using Jackson
    ObjectMapper mapper = new ObjectMapper();
    Page page = mapper.readValue(responseStream, Page.class);

    // Finally we have the response
    page.printUsers();
  }
}
