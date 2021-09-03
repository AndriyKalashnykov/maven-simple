package http.client.okhttp;

import com.fasterxml.jackson.databind.ObjectMapper;
import http.client.model.Page;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class OkHttpDemo {

    protected static final Logger LOGGER = LoggerFactory.getLogger(OkHttpDemo.class);

    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://jsonmock.hackerrank.com/api/article_users?page=2")
                .build(); // defaults to GET

        Response response = client.newCall(request).execute();

        Page page = mapper.readValue(response.body().byteStream(), Page.class);

        page.printUsers();

    }

}