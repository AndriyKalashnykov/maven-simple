package http.client.java;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Offline smoke test: runs {@link JavaHttpURLConnectionDemo#main} end-to-end against an in-process
 * WireMock stub (URL injected via the {@code articleUsersUrl} system property) so {@code make test}
 * exercises the demo wiring without a live network call. Contract assertions on the parsed response
 * live in {@link JavaHttpUrlConnectionIT}.
 */
@WireMockTest
class JavaHttpURLConnectionDemoTest {

  private static final Path FIXTURE =
      Path.of("src/test/resources/fixtures/article-users-page2.json");

  @Test
  @DisplayName("main() fetches and parses the page from a stubbed server")
  void mainAgainstStub(WireMockRuntimeInfo wm) throws Exception {
    stubFor(
        get(urlPathEqualTo("/api/article_users"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(Files.readString(FIXTURE))));
    System.setProperty("articleUsersUrl", wm.getHttpBaseUrl() + "/api/article_users?page=2");
    try {
      JavaHttpURLConnectionDemo.main(new String[] {});
    } finally {
      System.clearProperty("articleUsersUrl");
    }
    verify(getRequestedFor(urlPathEqualTo("/api/article_users")));
  }
}
