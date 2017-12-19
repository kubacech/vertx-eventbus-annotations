package com.github.kubacech.vertx.eventbus.rxjava;

import io.vertx.core.Context;
import io.vertx.core.Vertx;

public class AnnotatedAbstractVerticle extends com.github.kubacech.vertx.eventbus.AnnotatedAbstractVerticle {

    protected io.vertx.rxjava.core.Vertx vertx;

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        this.vertx = new io.vertx.rxjava.core.Vertx(vertx);
    }
}
