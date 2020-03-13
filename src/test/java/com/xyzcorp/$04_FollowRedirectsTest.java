package com.xyzcorp;

import org.junit.jupiter.api.Test;

import java.net.Authenticator;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.Base64;

public class $04_FollowRedirectsTest {

    @Test
    void testFollowingRedirects() throws InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        String username = "adumbledore";
        String password = "occlumens";

        StringBuilder builder = new StringBuilder();
        builder.append(username);
        builder.append(":");
        builder.append(password);

        Base64.Encoder encoder = Base64.getEncoder();
        HttpRequest request =
            HttpRequest
                .newBuilder()
                .timeout(Duration.ofMinutes(2))
                .uri(URI.create("http://localhost:8080/fruits"))
                .header("Authentication", encoder.encodeToString(builder.toString().getBytes()))
                .build();


        HttpClient.newBuilder()
                  .authenticator(Authenticator.getDefault())
                  .followRedirects(HttpClient.Redirect.ALWAYS);

        Thread.sleep(2000);
    }
}
