package http.client.okhttp;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import http.client.model.Page;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

@WireMockTest
class OkHttpClientIT {

  private static final Path FIXTURE =
      Path.of("src/test/resources/fixtures/article-users-page2.json");

  private final OkHttpClient client = new OkHttpClient();
  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  @DisplayName("parses Page response from stubbed server")
  void fetchPage_success(WireMockRuntimeInfo wm) throws IOException {
    String fixture = Files.readString(FIXTURE);
    stubFor(
        get(urlPathEqualTo("/api/article_users"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(fixture)));

    Request request =
        new Request.Builder().url(wm.getHttpBaseUrl() + "/api/article_users?page=2").build();

    try (Response response = client.newCall(request).execute()) {
      assertEquals(200, response.code());
      Page page = mapper.readValue(response.body().byteStream(), Page.class);
      assertEquals(2, page.getPage());
      assertEquals(10, page.getPerPage());
      assertNotNull(page.getData());
      assertEquals(2, page.getData().size());
      assertEquals("brynn", page.getData().get(0).getUsername());
    }
  }

  @Test
  @DisplayName("surfaces 429 rate-limit as non-successful response")
  void fetchPage_rateLimited(WireMockRuntimeInfo wm) throws IOException {
    stubFor(get(urlPathEqualTo("/api/article_users")).willReturn(aResponse().withStatus(429)));

    Request request = new Request.Builder().url(wm.getHttpBaseUrl() + "/api/article_users").build();

    try (Response response = client.newCall(request).execute()) {
      assertFalse(response.isSuccessful());
      assertEquals(429, response.code());
    }
  }
}
