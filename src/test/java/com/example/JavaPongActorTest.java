package com.example;


import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import org.junit.Test;
import scala.concurrent.Future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;

import static akka.pattern.Patterns.ask;
import static org.assertj.core.api.Assertions.assertThat;
import static scala.compat.java8.FutureConverters.toJava;

public class JavaPongActorTest {
    ActorSystem system = ActorSystem.create();
    ActorRef actorRef = system.actorOf(Props.create(JavaPongActor.class));

    @Test
    public void should_reply_to_ping_with_pong() throws Exception {
        final CompletionStage<String> cs = askPong("Ping");
        final CompletableFuture<String> jFuture = (CompletableFuture<String>) cs;

        assertThat(jFuture.get(1000, TimeUnit.MILLISECONDS)).isEqualTo("Pong");
    }

    @Test
    public void printToConsole() throws InterruptedException {
        askPong("Ping")
                .thenAccept(x -> System.out.println("replied with " + x));
        Thread.sleep(100);
    }

    @Test
    public void shouldReturnFirstChar() {
        askPong("Ping")
                .thenApply(x -> x.charAt(0));
    }

    @Test
    public void shouldTranformTheResultAsynchronously() {
        //thenCompose: flattens CompletionState[CompletionStage[String]]
        final CompletionStage<String> cs = askPong("Ping").thenCompose(x -> askPong("Ping"));
    }

    @Test
    public void shouldHandleFailure() {
        BiFunction<String, Throwable, Object> throwableByString = (s, t) -> {
            if (t != null) {
                System.out.println("Error: " + t);
            }
            return null;
        };

        askPong("causeError").handle(throwableByString);
    }

    @Test
    public void shouldRecoverFromFailure() {
        final Function<Throwable, String> alwaysDefaultFunction = t -> "default";

        askPong("causeError").exceptionally(alwaysDefaultFunction);
    }

    @Test
    public void shouldRecoverFromFailureAsynchronously() {
        askPong("causeError")
                .handle((pong, exception) -> exception == null ? CompletableFuture.completedFuture(pong) : askPong("Ping"))
                .thenCompose(x -> x);
    }

    private CompletionStage<String> askPong(String msg) {
        Future sFuture = ask(actorRef, msg, 1000);
        return toJava(sFuture);
    }

}
