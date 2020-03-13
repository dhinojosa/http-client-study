package com.xyzcorp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.rxjava3.core.Flowable;
import org.junit.jupiter.api.Test;
import org.reactivestreams.FlowAdapters;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;

public class $03_BodyHandlersTest {

    private static List<Fruit> uncheckedGetFruit(ObjectMapper mapper,
                                                 String s) {
        try {
            return mapper.readValue(s, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetWithJson() throws InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper();

        HttpRequest request =
            HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8080/fruits"))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
              .thenApply(HttpResponse::body)
              .thenApply(s -> uncheckedGetFruit(mapper, s))
              .exceptionally(t -> List.of())
              .thenAccept(System.out::println);
        Thread.sleep(2000);
    }

    @Test
    void testPostWithJSON() throws JsonProcessingException,
        InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper();

        Fruit watermelon = new Fruit("Watermelon", "Delicious Summer Treat");

        String body = mapper.writeValueAsString(watermelon);

        HttpRequest request =
            HttpRequest
                .newBuilder()
                .header("content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .uri(URI.create("http://localhost:8080/fruits"))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
              .thenApply(HttpResponse::body)
              .thenApply(s -> uncheckedGetFruit(mapper, s))
              .exceptionally(t -> {
                  t.printStackTrace();
                  return List.of();
              })
              .thenAccept(System.out::println);
        Thread.sleep(2000);
    }

    @Test
    void testPostWithJSONWithPairStatusAndContent() throws JsonProcessingException,
        InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper();

        Fruit watermelon = new Fruit("Canteloupe", "Nice, Juicy");

        String body = mapper.writeValueAsString(watermelon);

        HttpRequest request =
            HttpRequest
                .newBuilder()
                .header("content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .uri(URI.create("http://localhost:8080/fruits"))
                .build();

        CompletableFuture<HttpResponse<String>> resp =
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        CompletableFuture<Integer> statusCode =
            resp.thenApply(HttpResponse::statusCode);

        CompletableFuture<String> content =
            resp.thenApply(HttpResponse::body);

        CompletableFuture<Pair<Integer, List<Fruit>>> pairCompletableFuture =
            statusCode.thenCompose(sc ->
                content.thenApply(b -> new Pair<>(sc
                    , uncheckedGetFruit(mapper, b))));

        CompletableFuture<Pair<Integer, List<Fruit>>> exceptionally =
            pairCompletableFuture.exceptionally(t -> new Pair<>(100,
                List.of()));

        exceptionally.thenAccept(System.out::println);

        Thread.sleep(2000);
    }

    //TODO: A benefit of using a Publisher is taking content
    //      and forking and being able to dispose of a Flowable
    //      Publishers are also standards and can be plugged into
    //      your favorite
    @Test
    void testPostWithJSONWithPublisherAPI() throws JsonProcessingException,
        InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper();

        Fruit watermelon = new Fruit("Tangelo", "What the **** is this thing?");

        String body = mapper.writeValueAsString(watermelon);

        HttpRequest request =
            HttpRequest
                .newBuilder()
                .header("content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .uri(URI.create("http://localhost:8080/fruits"))
                .build();

        CompletableFuture<HttpResponse<Flow.Publisher<List<ByteBuffer>>>> future =
            client.sendAsync(request, HttpResponse.BodyHandlers.ofPublisher());

        future
            .thenApply(resp -> FlowAdapters.toPublisher(resp.body()))
            .thenApply(Flowable::fromPublisher)
            .thenApply(flowable ->
                flowable.flatMap(Flowable::fromIterable))
            .thenApply(flowable ->
                flowable.map(bb -> StandardCharsets.UTF_8.decode(bb).toString()))
            .thenApply(flowable ->
                flowable.flatMap(s -> Flowable.fromIterable(uncheckedGetFruit(mapper, s))))
            .thenApply(flowable ->
                flowable.subscribe(System.out::println,
                Throwable::printStackTrace))
            .thenAccept(disposable -> {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                disposable.dispose();
            });
        Thread.sleep(3000);
    }
}
