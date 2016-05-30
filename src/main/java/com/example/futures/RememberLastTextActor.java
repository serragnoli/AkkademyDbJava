package com.example.futures;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;

public class RememberLastTextActor extends AbstractActor {
    private LoggingAdapter log = Logging.getLogger(context().system(), this);

    private String lastText;

    private RememberLastTextActor() {
        receive(
                ReceiveBuilder
                        .match(RememberMe.class, m -> {
                            log.info("Next message to remember is {}", m.getText());
                            lastText = m.getText();
                        })
                        .matchAny(o -> log.info("Unknown message received {}", o))
                        .build()
        );
    }

    public String getLastText() {
        return lastText;
    }
}
