package com.github.kubacech.vertx.eventbus;

import io.reactivex.Completable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.reactivex.core.streams.ReadStream;

public class MessageConsumer<T> {

    private final io.vertx.reactivex.core.eventbus.MessageConsumer delegate;

    public MessageConsumer(io.vertx.reactivex.core.eventbus.MessageConsumer delegate) {
        this.delegate = delegate;
    }

    public MessageConsumer(io.vertx.core.eventbus.MessageConsumer delegate) {
        this.delegate = new io.vertx.reactivex.core.eventbus.MessageConsumer(delegate);
    }

    public MessageConsumer(io.vertx.rxjava.core.eventbus.MessageConsumer delegate) {
        this.delegate = new io.vertx.reactivex.core.eventbus.MessageConsumer(delegate.getDelegate());
    }


    public MessageConsumer<T> exceptionHandler(Handler<Throwable> handler) {
        delegate.exceptionHandler(handler);
        return this;
    }

   public MessageConsumer<T> handler(Handler<Message<T>> handler) {
        delegate.handler(handler);
        return this;
    }

    public MessageConsumer<T> pause() {
        delegate.pause();
        return this;
    }

    public MessageConsumer<T> resume() {
        delegate.resume();
        return this;
    }

    public MessageConsumer<T> endHandler(Handler<Void> endHandler) {
        delegate.endHandler(endHandler);
        return this;
    }

    public ReadStream<T> bodyStream() {
        return delegate.bodyStream();
    }


    public String address() {
        return delegate.address();
    }

    public MessageConsumer<T> setMaxBufferedMessages(int maxBufferedMessages) {
        delegate.setMaxBufferedMessages(maxBufferedMessages);
        return this;
    }

    public int getMaxBufferedMessages() {
        return delegate.getMaxBufferedMessages();
    }

    public void completionHandler(Handler<AsyncResult<Void>> completionHandler) {
        delegate.completionHandler(completionHandler);
    }

    public Completable rxCompletionHandler() {
        return delegate.rxCompletionHandler();
    }

    public void unregister() {
        delegate.unregister();
    }

    public void unregister(Handler<AsyncResult<Void>> completionHandler) {
        delegate.unregister(completionHandler);
    }

    /**
     * Unregisters the handler which created this registration
     */
    public Completable rxUnregister() {
        return delegate.rxUnregister();
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageConsumer that = (MessageConsumer) o;
        return delegate.equals(that.delegate);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }
}
