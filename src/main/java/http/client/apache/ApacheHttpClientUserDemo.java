package http.client.apache;

import http.client.model.Page;
import java.io.IOException;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.ObjectMapper;

public class ApacheHttpClientUserDemo {

  protected static final Logger LOGGER = LoggerFactory.getLogger(ApacheHttpClientUserDemo.class);

  private static final ObjectMapper mapper = new ObjectMapper();

  // Request target — defaults to the live demo API; overridable for offline tests
  // via -DarticleUsersUrl=... or the ARTICLE_USERS_URL env var.
  static final String ARTICLE_USERS_URL =
      System.getProperty(
          "articleUsersUrl",
          System.getenv()
              .getOrDefault(
                  "ARTICLE_USERS_URL", "https://jsonmock.hackerrank.com/api/article_users?page=2"));

  public static void main(String[] args) throws IOException {

    try (CloseableHttpClient client = HttpClients.createDefault()) {

      HttpGet request = new HttpGet(ARTICLE_USERS_URL);

      Page page =
          client.execute(
              request,
              httpResponse -> mapper.readValue(httpResponse.getEntity().getContent(), Page.class));

      page.printUsers();
    }
  }
}
