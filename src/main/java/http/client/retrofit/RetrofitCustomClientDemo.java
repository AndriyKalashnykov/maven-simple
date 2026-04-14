package http.client.retrofit;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import http.client.model.Page;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public class RetrofitCustomClientDemo {

  protected static final Logger LOGGER = LoggerFactory.getLogger(RetrofitCustomClientDemo.class);

  public interface PageClient {
    @GET("/api/article_users?page=2")
    @Headers("accept: application/json")
    CompletableFuture<Page> getPage();
  }

  public static void main(String[] args) throws ExecutionException, InterruptedException {

    Gson gson =
        new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    Retrofit retrofit =
        new Retrofit.Builder()
            .baseUrl("https://jsonmock.hackerrank.com")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();

    PageClient pageClient = retrofit.create(PageClient.class);

    Page page = pageClient.getPage().get();

    page.printUsers();
  }
}
