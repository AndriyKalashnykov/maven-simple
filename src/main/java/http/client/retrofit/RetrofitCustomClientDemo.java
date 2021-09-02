package http.client.retrofit;

import http.client.model.Page;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class RetrofitCustomClientDemo {


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

        System.out.println(page.getPage().toString());
    }

}