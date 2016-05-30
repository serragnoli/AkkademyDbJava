package com.example.futures;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.TestActorRef;
import com.example.futures.AkkademyDb;
import com.example.futures.SetRequest;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class AkkademyDbTest {

    private ActorSystem system = ActorSystem.create();

    @Test
    public void should_add_key_value_into_map() {
        TestActorRef<AkkademyDb> actorRef = TestActorRef.create(system, Props.create(AkkademyDb.class));

        actorRef.tell(new SetRequest("key", "value"), ActorRef.noSender());

        AkkademyDb akkademyDb = actorRef.underlyingActor();
        assertThat(akkademyDb.map.get("key")).isEqualTo("value");
    }
}