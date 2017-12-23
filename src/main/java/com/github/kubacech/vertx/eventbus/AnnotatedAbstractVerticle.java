package com.github.kubacech.vertx.eventbus;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

public abstract class AnnotatedAbstractVerticle extends AbstractVerticle {

    private EventAnnotationProcessor eventAnnotationProcessor;

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        this.eventAnnotationProcessor = new EventAnnotationProcessor(vertx);
        this.eventAnnotationProcessor.process(this);
    }

}
