package com.github.kubacech.vertx.eventbus;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

public abstract class AnnotatedAbstractVerticle extends AbstractVerticle {

    private EventAnnotationProcessor eventAnnotationProcessor;

    @Override
    public void start() throws Exception {
         this.eventAnnotationProcessor = new EventAnnotationProcessor(vertx);
         this.eventAnnotationProcessor.process(this);
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        this.start();
        startFuture.complete();
    }

}
