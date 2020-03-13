package com.xyzcorp;

import org.junit.jupiter.api.Test;

import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;

public class $04_AuthenticationTest {

    @Test
    void testBasicAuthentication() throws InterruptedException {

        String username = "user";
        String password = "user";

        StringBuilder builder = new StringBuilder();
        builder.append(username);
        builder.append(":");
        builder.append(password);

        Base64.Encoder encoder = Base64.getEncoder();
        HttpRequest request =
            HttpRequest
                .newBuilder()
                .timeout(Duration.ofMinutes(2))
                .uri(URI.create("http://localhost:8080/api/users/me"))
                .header("Authorization", "Basic " +
                    encoder.encodeToString(builder.toString().getBytes()))
                .build();

        HttpClient
            .newHttpClient()
            .sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply(resp -> new Pair<>(resp.statusCode(), resp.body()))
            .thenAccept(System.out::println);

        Thread.sleep(2000);
    }


    @Test
    void testBasicAuthenticationWithAuthenticator() throws InterruptedException, UnknownHostException {

        HttpRequest request =
            HttpRequest
                .newBuilder()
                .timeout(Duration.ofMinutes(2))
                .uri(URI.create("http://localhost:8080/api/users/me"))
                .build();

        HttpClient httpClient = HttpClient
            .newBuilder()
            .authenticator(new Authenticator() {
                @Override
                public PasswordAuthentication requestPasswordAuthenticationInstance(String host, InetAddress addr, int port, String protocol, String prompt, String scheme, URL url, RequestorType reqType) {
                    String username = "user";
                    String password = "usver";
                    return new PasswordAuthentication(username,
                        password.toCharArray());
                }
            }).build();

        httpClient
            .sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .exceptionally(throwable -> {throwable.printStackTrace(); return null;})
            .thenApply(resp -> new Pair<>(resp.statusCode(), resp.body()))
            .thenAccept(System.out::println);
        Thread.sleep(20000);
    }

    /** Test with Vault **/
    @Test
    @org.junit.jupiter.api.Disabled("Need to implement")
    void testWithVault() throws InterruptedException, UnknownHostException {

    }
}
