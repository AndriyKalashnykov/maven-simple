package http.client.okhttp;

import http.client.model.Page;
import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.ObjectMapper;

public class OkHttpDemo {

  protected static final Logger LOGGER = LoggerFactory.getLogger(OkHttpDemo.class);

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

    OkHttpClient client = new OkHttpClient();

    Request request = new Request.Builder().url(ARTICLE_USERS_URL).build(); // defaults to GET

    Response response = client.newCall(request).execute();

    Page page = mapper.readValue(response.body().byteStream(), Page.class);

    page.printUsers();
  }
}
