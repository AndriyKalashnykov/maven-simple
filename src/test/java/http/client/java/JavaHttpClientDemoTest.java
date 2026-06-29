package http.client.java;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.moreThanOrExactly;
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
 * Offline smoke test: runs {@link JavaHttpClientDemo#main} (synchronous + asynchronous requests)
 * end-to-end against an in-process WireMock stub so {@code make test} exercises the demo wiring
 * without a live network call. Contract assertions live in {@link JavaHttpClientIT}.
 */
@WireMockTest
class JavaHttpClientDemoTest {

  private static final Path FIXTURE =
      Path.of("src/test/resources/fixtures/article-users-page2.json");

  @Test
  @DisplayName("main() runs sync + async requests against a stubbed server")
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
      JavaHttpClientDemo.main(new String[] {});
    } finally {
      System.clearProperty("articleUsersUrl");
    }
    // main() issues both a synchronous and an asynchronous request.
    verify(moreThanOrExactly(2), getRequestedFor(urlPathEqualTo("/api/article_users")));
  }
}
