package http.client.java;

import http.client.model.Page;
import http.client.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;


public class JavaHttpClientDemo {

    protected static final Logger LOGGER = LoggerFactory.getLogger(JavaHttpClientDemo.class);

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        synchronousRequest();
        asynchronousRequest();
    }

    public static void printUsers(final Page page) {

        boolean printAll = false;
        List<User> users = page.getData();
        Iterator<User> i = users.iterator();

        int threshold = 10;

        while (i.hasNext()) {
            User user  = (User) i.next();
            if (user.getCommentCount().compareTo(Integer.valueOf(String.valueOf(threshold))) > 0) {
                LOGGER.info(user.getUsername().toString() + " : " + user.getCommentCount().toString());
            }
        }

        if (printAll) {
            for (User user : users) {
                LOGGER.info(user.getSubmissionCount().toString());
            }
        }
    }

    private static void asynchronousRequest() throws InterruptedException, ExecutionException {

        // create a client
        var client = HttpClient.newHttpClient();

        // create a request
        var request = HttpRequest.newBuilder(
                        URI.create("https://jsonmock.hackerrank.com/api/article_users?page=2"))
                .header("accept", "application/json")
                .build();

        // use the client to send the request
        var responseFuture = client.sendAsync(request, new JsonBodyHandler<>(Page.class));

        // We can do other things here while the request is in-flight

        // This blocks until the request is complete
        var response = responseFuture.get();

        // the response:
        printUsers(response.body().get());
    }

    private static void synchronousRequest() throws IOException, InterruptedException {
        // create a client
        var client = HttpClient.newHttpClient();

        // create a request
        var request = HttpRequest.newBuilder(
                URI.create("https://jsonmock.hackerrank.com/api/article_users?page=2")
        ).build();

        // use the client to send the request
        HttpResponse<Supplier<Page>> response = client.send(request, new JsonBodyHandler<>(Page.class));

        // the response:
        printUsers(response.body().get());
    }

}
