package com.github.kubacech.vertx.eventbus;

import com.github.kubacech.vertx.eventbus.annotation.EventAnnotationProcessor;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Vertx;

public abstract class AnnotatedAbstractVerticle extends AbstractVerticle {

    private TypedEventBus eventBus;
    private EventAnnotationProcessor eventAnnotationProcessor;

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        this.eventBus = new TypedEventBus(vertx);
        this.eventAnnotationProcessor = new EventAnnotationProcessor(vertx);
        this.eventAnnotationProcessor.process(this);
    }

    protected TypedEventBus eventBus() {
        return eventBus;
    }

}
