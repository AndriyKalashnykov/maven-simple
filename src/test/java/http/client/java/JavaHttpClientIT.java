package http.client.java;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import http.client.model.Page;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Supplier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * WireMock integration test for the {@code java.net.http.HttpClient} client + the project's {@link
 * JsonBodyHandler} (production class) parse into {@link Page}, mirroring {@link JavaHttpClientDemo}
 * against a stubbed server (deterministic, offline) instead of the live upstream API.
 */
@WireMockTest
class JavaHttpClientIT {

  private static final Path FIXTURE =
      Path.of("src/test/resources/fixtures/article-users-page2.json");

  @Test
  @DisplayName("parses Page response via JsonBodyHandler from stubbed server")
  void fetchPage_success(WireMockRuntimeInfo wm) throws IOException, InterruptedException {
    stubFor(
        get(urlPathEqualTo("/api/article_users"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(Files.readString(FIXTURE))));

    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request =
        HttpRequest.newBuilder(URI.create(wm.getHttpBaseUrl() + "/api/article_users?page=2"))
            .header("accept", "application/json")
            .build();

    HttpResponse<Supplier<Page>> response = client.send(request, new JsonBodyHandler<>(Page.class));

    assertEquals(200, response.statusCode());
    Page page = response.body().get();
    assertEquals(2, page.getPage());
    assertEquals(10, page.getPerPage());
    assertEquals(42, page.getTotal());
    assertNotNull(page.getData());
    assertEquals(2, page.getData().size());
    assertEquals("brynn", page.getData().get(0).getUsername());
    assertEquals(47, page.getData().get(0).getCommentCount());
  }

  @Test
  @DisplayName("surfaces 429 rate-limit via status code")
  void fetchPage_rateLimited(WireMockRuntimeInfo wm) throws IOException, InterruptedException {
    stubFor(get(urlPathEqualTo("/api/article_users")).willReturn(aResponse().withStatus(429)));

    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request =
        HttpRequest.newBuilder(URI.create(wm.getHttpBaseUrl() + "/api/article_users")).build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    assertEquals(429, response.statusCode());
  }
}
