package http.client.retrofit;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import http.client.model.Page;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import retrofit2.HttpException;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;

/**
 * WireMock integration test for the Retrofit client + Gson converter parse into {@link Page},
 * mirroring {@link RetrofitDemo}/{@link RetrofitCustomClientDemo} (same {@code PageClient} shape
 * and {@code LOWER_CASE_WITH_UNDERSCORES} naming) against a stubbed server (deterministic, offline)
 * instead of the live upstream API.
 */
@WireMockTest
class RetrofitClientIT {

  private static final Path FIXTURE =
      Path.of("src/test/resources/fixtures/article-users-page2.json");

  interface PageClient {
    @GET("/api/article_users?page=2")
    @Headers("accept: application/json")
    CompletableFuture<Page> getPage();
  }

  private static PageClient client(WireMockRuntimeInfo wm) {
    Gson gson =
        new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();
    Retrofit retrofit =
        new Retrofit.Builder()
            .baseUrl(wm.getHttpBaseUrl() + "/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();
    return retrofit.create(PageClient.class);
  }

  @Test
  @DisplayName("parses Page response from stubbed server")
  void fetchPage_success(WireMockRuntimeInfo wm)
      throws IOException, ExecutionException, InterruptedException {
    stubFor(
        get(urlPathEqualTo("/api/article_users"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(Files.readString(FIXTURE))));

    Page page = client(wm).getPage().get();

    assertEquals(2, page.getPage());
    assertEquals(10, page.getPerPage());
    assertEquals(42, page.getTotal());
    assertNotNull(page.getData());
    assertEquals(2, page.getData().size());
    assertEquals("brynn", page.getData().get(0).getUsername());
    assertEquals(47, page.getData().get(0).getCommentCount());
  }

  @Test
  @DisplayName("surfaces 429 rate-limit as HttpException")
  void fetchPage_rateLimited(WireMockRuntimeInfo wm) {
    stubFor(get(urlPathEqualTo("/api/article_users")).willReturn(aResponse().withStatus(429)));

    ExecutionException thrown =
        assertThrows(ExecutionException.class, () -> client(wm).getPage().get());
    HttpException cause = assertInstanceOf(HttpException.class, thrown.getCause());
    assertEquals(429, cause.code());
  }
}
