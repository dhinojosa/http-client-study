package com.xyzcorp;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class $02_GetContentStringAsyncTest {

    @Test
    @DisplayName("Run an example connecting to a simple" +
        " web endpoint using GET asynchronously and returning a String")
    void testSimpleAsyncGet() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request =
            HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8080/hello"))
                .build();

        CompletableFuture<HttpResponse<String>> future =
            client.sendAsync(request,
            HttpResponse.BodyHandlers.ofString());

        future
            .thenApply(stringHttpResponse -> stringHttpResponse.body())
            .thenAccept(System.out::println);

        Thread.sleep(1000);
    }
}
