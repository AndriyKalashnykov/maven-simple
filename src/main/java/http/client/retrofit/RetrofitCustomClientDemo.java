package http.client.retrofit;

import http.client.model.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class RetrofitCustomClientDemo {

    protected static final Logger LOGGER = LoggerFactory.getLogger(RetrofitCustomClientDemo.class);

    public interface PageClient {
        @GET("/api/article_users?page=2")
        @Headers("accept: application/json")
        CompletableFuture<Page> getPage();
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://jsonmock.hackerrank.com")
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        PageClient pageClient = retrofit.create(PageClient.class);

        Page page = pageClient.getPage().get();

        page.printUsers();
    }

}