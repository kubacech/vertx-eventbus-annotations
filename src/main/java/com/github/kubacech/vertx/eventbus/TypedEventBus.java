package com.github.kubacech.vertx.eventbus;

import io.reactivex.Single;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.Json;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.core.eventbus.Message;

public class TypedEventBus {

    private io.vertx.reactivex.core.eventbus.EventBus delegate;

    public TypedEventBus(io.vertx.core.Vertx vertx) {
        this.delegate = new EventBus(vertx.eventBus());
    }

    public TypedEventBus(Vertx vertx) {
        this.delegate = vertx.eventBus();
    }

    public TypedEventBus(io.vertx.rxjava.core.Vertx vertx) {
        this.delegate = new EventBus(vertx.eventBus().getDelegate());
    }


    public void publish(Object message) {
        this.delegate.publish(message.getClass().getName(), message, deliveryOpts());
    }

    public void publish(Object message, DeliveryOptions opts) {
        opts.setCodecName(JacksonMessageCodec.class.getName());
        this.delegate.publish(message.getClass().getName(), message, opts);
    }

    public void publish(String address, Object message, DeliveryOptions opts) {
        opts.setCodecName(JacksonMessageCodec.class.getName());
        this.delegate.publish(address, message, opts);
    }

    public <T> void send(Object message) {
        this.delegate.send(message.getClass().getName(), message, deliveryOpts());
    }

    public <T> void send(String address, Object message) {
        this.delegate.send(address, message, deliveryOpts());
    }

    public <T> void send(Object message, DeliveryOptions opts) {
        opts.setCodecName(JacksonMessageCodec.class.getName());
        this.delegate.send(message.getClass().getName(), message, opts);
    }

    public <T> void send(String address, Object message, DeliveryOptions opts) {
        opts.setCodecName(JacksonMessageCodec.class.getName());
        this.delegate.send(address, message, opts);
    }


    public <T> Single<T> askFor(Object message) {
        return this.delegate.rxSend(message.getClass().getName(), message, deliveryOpts())
                .map(tMessage -> (T)tMessage.body());
    }

    public <T> Single<T> askFor(String address, Object message) {
        return this.delegate.rxSend(address, message, deliveryOpts())
                .map(tMessage -> (T)tMessage.body());
    }

    public <T> Single<T> askFor(Object message, DeliveryOptions opts, Class<T> returnType) {
        opts.setCodecName(JacksonMessageCodec.class.getName());
        return this.delegate.rxSend(message.getClass().getName(), message, opts)
                .map(tMessage -> decodeResponse(tMessage, returnType));
    }

    public <T> Single<T> askFor(String address, Object message, DeliveryOptions opts, Class<T> returnType) {
        opts.setCodecName(JacksonMessageCodec.class.getName());
        return this.delegate.rxSend(address, message, opts)
                .map(tMessage -> decodeResponse(tMessage, returnType));
    }

    private <T> T decodeResponse(Message<Object> responseMessage, Class<T> returnType) {
        Object response = responseMessage.body();
        if (response instanceof String) {
            return Json.decodeValue((String) response, returnType);
        }
        if (response.getClass().equals(returnType)) {
            return (T)response;
        }
        throw new IllegalArgumentException("Cannot decode return type of " + response.getClass().getName());
    }

    private DeliveryOptions deliveryOpts() {
        return new DeliveryOptions().setCodecName(JacksonMessageCodec.class.getName());
    }
}
