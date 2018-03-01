package com.github.kubacech.vertx.eventbus;

import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.reactivex.core.MultiMap;

/**
 * Utility class to encode reply to String (with Jackson) before really sending it to eventbus
 * @param <T>
 */
public class Message<T> {

    private io.vertx.reactivex.core.eventbus.Message<T> delegate;

    public Message(io.vertx.reactivex.core.eventbus.Message message) {
        this.delegate = message;
    }

    public Message(io.vertx.core.eventbus.Message message) {
        this.delegate = new io.vertx.reactivex.core.eventbus.Message<>(message);
    }

    public Message(io.vertx.rxjava.core.eventbus.Message message) {
        this.delegate = new io.vertx.reactivex.core.eventbus.Message<>(message.getDelegate());
    }

    public String address() {
        return this.delegate.address();
    }

    public MultiMap headers() {
        return this.delegate.headers();
    }

    public String replyAddress() {
        return this.delegate.replyAddress();
    }

    public boolean isSend() {
        return this.delegate.isSend();
    }

    public T body() {
        return delegate.body();
    }

    public void reply(Object message) {

        delegate.reply(message, new DeliveryOptions().setCodecName(JacksonMessageCodec.class.getName()));
    }

    public void fail(int failureCode, String message) {
        delegate.fail(failureCode, message);
    }

    public static <T> Message<T> newInstance(io.vertx.core.eventbus.Message arg) {
        return arg != null ? new Message<>(arg) : null;
    }

}
