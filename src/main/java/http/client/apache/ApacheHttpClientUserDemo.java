package http.client.apache;

import com.fasterxml.jackson.databind.ObjectMapper;
import http.client.model.Page;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ApacheHttpClientUserDemo {

    protected static final Logger LOGGER = LoggerFactory.getLogger(ApacheHttpClientUserDemo.class);

    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {

        try (CloseableHttpClient client = HttpClients.createDefault()) {

            HttpGet request = new HttpGet("https://jsonmock.hackerrank.com/api/article_users?page=2");

            Page page = client.execute(request, httpResponse ->
                    mapper.readValue(httpResponse.getEntity().getContent(), Page.class));

            page.printUsers();
        }

    }

}