package http.client.java;

import com.fasterxml.jackson.databind.ObjectMapper;
import http.client.model.Page;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class JavaHttpURLConnectionDemo {

    public static void main(String[] args) throws IOException {

        // Create a neat value object to hold the URL
        URL url = new URL("https://jsonmock.hackerrank.com/api/article_users?page=2");

        // Open a connection(?) on the URL(?) and cast the response(??)
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Now it's "open", we can set the request method, headers etc.
        connection.setRequestProperty("accept", "application/json");

        // This line makes the request
        InputStream responseStream = connection.getInputStream();

        // Manually converting the response body InputStream to APOD using Jackson
        ObjectMapper mapper = new ObjectMapper();
        Page apod = mapper.readValue(responseStream, Page.class);

        // Finally we have the response
        System.out.println(apod.getPage().toString());

    }

}