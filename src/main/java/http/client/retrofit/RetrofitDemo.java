package http.client.retrofit;

import http.client.model.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class RetrofitDemo {

    protected static final Logger LOGGER = LoggerFactory.getLogger(RetrofitDemo.class);

    public interface PageClient {
        @GET("/api/article_users?page=2")
        @Headers("accept: application/json")
        CompletableFuture<Page> getPage(@Query("api_key") String apiKey);
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://jsonmock.hackerrank.com")
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        PageClient pageClient = retrofit.create(PageClient.class);

        CompletableFuture<Page> response = pageClient.getPage("DEMO_KEY");

        // do other stuff here while the request is in-flight

        Page page = response.get();

        page.printUsers();

    }

}