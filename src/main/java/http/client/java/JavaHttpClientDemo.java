package http.client.java;

import http.client.model.Page;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaHttpClientDemo {

  protected static final Logger LOGGER = LoggerFactory.getLogger(JavaHttpClientDemo.class);

  // Request target — defaults to the live demo API; overridable for offline tests
  // via -DarticleUsersUrl=... or the ARTICLE_USERS_URL env var.
  static final String ARTICLE_USERS_URL =
      System.getProperty(
          "articleUsersUrl",
          System.getenv()
              .getOrDefault(
                  "ARTICLE_USERS_URL", "https://jsonmock.hackerrank.com/api/article_users?page=2"));

  public static void main(String[] args)
      throws IOException, InterruptedException, ExecutionException {
    synchronousRequest();
    asynchronousRequest();
  }

  private static void asynchronousRequest() throws InterruptedException, ExecutionException {

    // create a client
    var client = HttpClient.newHttpClient();

    // create a request
    var request =
        HttpRequest.newBuilder(URI.create(ARTICLE_USERS_URL))
            .header("accept", "application/json")
            .build();

    // use the client to send the request
    var responseFuture = client.sendAsync(request, new JsonBodyHandler<>(Page.class));

    // We can do other things here while the request is in-flight

    // This blocks until the request is complete
    var response = responseFuture.get();

    // the response:
    response.body().get().printUsers();
  }

  private static void synchronousRequest() throws IOException, InterruptedException {
    // create a client
    var client = HttpClient.newHttpClient();

    // create a request
    var request = HttpRequest.newBuilder(URI.create(ARTICLE_USERS_URL)).build();

    // use the client to send the request
    HttpResponse<Supplier<Page>> response = client.send(request, new JsonBodyHandler<>(Page.class));

    // the response:
    response.body().get().printUsers();
  }
}
