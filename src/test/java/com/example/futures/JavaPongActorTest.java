package com.example.futures;


import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.example.futures.JavaPongActor;
import org.junit.Test;
import scala.concurrent.Future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiFunction;
import java.util.function.Function;

import static akka.pattern.Patterns.ask;
import static java.lang.String.valueOf;
import static org.assertj.core.api.Assertions.assertThat;
import static scala.compat.java8.FutureConverters.toJava;

public class JavaPongActorTest {
    ActorSystem system = ActorSystem.create();
    ActorRef actorRef = system.actorOf(Props.create(JavaPongActor.class));

    @Test
    public void should_reply_to_ping_with_pong() throws Exception {
        final CompletableFuture<String> cs = askPong("Ping");

        assertThat(cs.get(1000, TimeUnit.MILLISECONDS)).isEqualTo("Pong");
    }

    @Test
    public void printToConsole() throws InterruptedException {
        askPong("Ping")
                .thenAccept(x -> System.out.println("replied with " + x));
        Thread.sleep(100);
    }

    @Test
    public void shouldReturnFirstChar() throws InterruptedException, ExecutionException, TimeoutException {
        final CompletableFuture<String> pong = askPong("Ping")
                .thenApply(x -> valueOf(x.charAt(0)));

        System.out.println("shouldReturnFirstChar: " + pong.get(1000, TimeUnit.MILLISECONDS));
    }

    @Test
    public void shouldTranformTheResultAsynchronously() throws InterruptedException, ExecutionException, TimeoutException {
        //thenCompose: flattens CompletionState[CompletionStage[String]]
        final CompletableFuture<String> pong = askPong("Ping")
                .thenCompose(x -> askPong("Ping"));

        System.out.println("shouldTranformTheResultAsynchronously: " + pong.get(1000, TimeUnit.MILLISECONDS));
    }

    @Test
    public void shouldHandleFailure() throws InterruptedException, ExecutionException, TimeoutException {
        BiFunction<String, Throwable, Object> throwableByString = (s, t) -> {
            if (t != null) {
                System.out.println("Error: " + t);
            }
            return null;
        };

        final CompletableFuture<Object> pong = askPong("causeError")
                .handle(throwableByString);

        System.out.println("shouldHandleFailure: " + pong.get(1000, TimeUnit.MILLISECONDS));
    }

    @Test
    public void shouldRecoverFromFailure() throws InterruptedException, ExecutionException, TimeoutException {
        final Function<Throwable, String> alwaysDefaultFunction = t -> "default";

        final CompletableFuture<String> pong = askPong("causeError")
                .exceptionally(alwaysDefaultFunction);

        System.out.println("shouldRecoverFromFailure: " + pong.get(1000, TimeUnit.MILLISECONDS));
    }

    @Test
    public void shouldRecoverFromFailureAsynchronously() throws InterruptedException, ExecutionException, TimeoutException {
        final CompletableFuture<String> pong = askPong("causeError")
                .handle((result, exception) -> exception == null
                        ? CompletableFuture.completedFuture(result)
                        : askPong("Ping"))
                .thenCompose(x -> x);

        System.out.println("shouldRecoverFromFailureAsynchronously: " + pong.get(1000, TimeUnit.MILLISECONDS));
    }

    @Test
    public void shouldChainOperationsTogether() throws InterruptedException, ExecutionException, TimeoutException {
        final CompletableFuture<String> pong = askPong("Ping")
                .thenCompose(x -> askPong("Ping" + x))
                .handle((x, throwable) -> {
                    if (throwable == null) {
                        return x;
                    }
                    return "default";
                });

        System.out.println("shouldChainOperationsTogether: " + pong.get(1000, TimeUnit.MILLISECONDS));
    }

    @Test
    public void shouldCombineTwoFuturesTogether() throws InterruptedException, ExecutionException, TimeoutException {
        final CompletableFuture<String> pong = askPong("Ping")
                .thenCombine(askPong("Ping"), (firstResult, secondResult) -> firstResult + secondResult);

        System.out.println("shouldCombineTwoFuturesTogether: " + pong.get(1000, TimeUnit.MILLISECONDS));
    }

    @SuppressWarnings("unchecked")
    private CompletableFuture<String> askPong(String msg) {
        Future sFuture = ask(actorRef, msg, 1000);
        return (CompletableFuture<String>) toJava(sFuture);
    }

}
