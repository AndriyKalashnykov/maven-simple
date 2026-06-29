package http.client.apache;

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
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

/**
 * WireMock integration test for the Apache HttpClient 5 client + Jackson parse into {@link Page},
 * mirroring {@link ApacheHttpClientUserDemo}'s response-handler usage against a stubbed server
 * (deterministic, offline) instead of the live upstream API.
 */
@WireMockTest
class ApacheHttpClientIT {

  private static final Path FIXTURE =
      Path.of("src/test/resources/fixtures/article-users-page2.json");

  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  @DisplayName("parses Page response from stubbed server")
  void fetchPage_success(WireMockRuntimeInfo wm) throws IOException {
    stubFor(
        get(urlPathEqualTo("/api/article_users"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(Files.readString(FIXTURE))));

    try (CloseableHttpClient client = HttpClients.createDefault()) {
      HttpGet request = new HttpGet(wm.getHttpBaseUrl() + "/api/article_users?page=2");
      Page page =
          client.execute(
              request,
              httpResponse -> mapper.readValue(httpResponse.getEntity().getContent(), Page.class));

      assertEquals(2, page.getPage());
      assertEquals(10, page.getPerPage());
      assertEquals(42, page.getTotal());
      assertNotNull(page.getData());
      assertEquals(2, page.getData().size());
      assertEquals("brynn", page.getData().get(0).getUsername());
      assertEquals(47, page.getData().get(0).getCommentCount());
    }
  }

  @Test
  @DisplayName("surfaces 429 rate-limit via response code")
  void fetchPage_rateLimited(WireMockRuntimeInfo wm) throws IOException {
    stubFor(get(urlPathEqualTo("/api/article_users")).willReturn(aResponse().withStatus(429)));

    try (CloseableHttpClient client = HttpClients.createDefault()) {
      HttpGet request = new HttpGet(wm.getHttpBaseUrl() + "/api/article_users");
      int code = client.execute(request, httpResponse -> httpResponse.getCode());
      assertEquals(429, code);
    }
  }
}
