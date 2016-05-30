package com.example.futures;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.TestActorRef;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RememberLastTextActorTest {

    private ActorSystem system = ActorSystem.create();

    @Test
    public void should_remember_the_last_text_sent() {
        TestActorRef<RememberLastTextActor> actorRef = TestActorRef.create(system, Props.create(RememberLastTextActor.class));

        actorRef.tell(new RememberMe("Text 1"), ActorRef.noSender());

        final RememberLastTextActor rememberLastTextActor = actorRef.underlyingActor();
        assertThat(rememberLastTextActor.getLastText()).isEqualTo("Text 1");

        actorRef.tell(new RememberMe("Text 2"), ActorRef.noSender());
        assertThat(rememberLastTextActor.getLastText()).isEqualTo("Text 2");
    }
}
