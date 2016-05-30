package com.example.futures;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class AkkademyDb extends AbstractActor {
    protected LoggingAdapter log = Logging.getLogger(context().system(), this);

    protected final Map<String, Object> map = new HashMap<>();

    private AkkademyDb() {
        receive(ReceiveBuilder
                .match(SetRequest.class, message -> {
                    log.info("Received set request - key: {} value: {}", message.getKey(), message.getValue());
                    map.put(message.getKey(), message.getValue());
                })
                .matchAny(o -> log.info("Received unknown message {}", o))
                .build());
    }


    public static void main(String[] args) {
        Map<String, Integer> ageByPerson = new HashMap<>();
        ageByPerson.put("Fabio", 37);
        ageByPerson.put("Karen", 36);
        ageByPerson.put("Isabella", 9);

        BiConsumer<String, Integer> biConsumer = (name, age) -> System.out.println(name + " " + age);

        ageByPerson.forEach(biConsumer);
        ageByPerson.forEach(
                (k, v) -> System.out.println(k + " " + v)
        );
    }
}
