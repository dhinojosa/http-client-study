package com.xyzcorp;

import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.security.NoSuchAlgorithmException;

public class $05_SSLTest {
    @Test
    void testSSLConnection() throws NoSuchAlgorithmException {
        SSLContext ssLv3 = SSLContext.getInstance("SSLv3");
        HttpClient httpClient =
            HttpClient.newBuilder()
                      .sslContext(ssLv3)
                      .sslParameters(ssLv3.getDefaultSSLParameters())
                      .build();

        HttpRequest build = HttpRequest.newBuilder().
        httpClient.send(build)

    }
}
