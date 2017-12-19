package com.github.kubacech.vertx.eventbus;

import com.github.kubacech.vertx.eventbus.annotation.EventConsumer;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class EventAnnotationProcessor {

    Logger LOG = LoggerFactory.getLogger(EventAnnotationProcessor.class);

    protected Vertx vertx;

    public EventAnnotationProcessor(Vertx vertx) {
        this.vertx = vertx;
    }

    public void process(Object object) {
        Class clazz = object.getClass();

        Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(EventConsumer.class))
                .forEach(method -> initEventConsumer(object, method));
    }

    private void initEventConsumer(Object o, Method m) {
        if (m.getParameterCount() != 1) {
            throw new IllegalArgumentException("Method annotated with " + EventConsumer.class.getName() + " must have one parameter");
        }

        EventConsumer annotation = m.getAnnotation(EventConsumer.class);
        Class parameterType = m.getParameters()[0].getType();

        if (parameterType == Message.class || parameterType == io.vertx.rxjava.core.eventbus.Message.class
                || parameterType == io.vertx.reactivex.core.eventbus.Message.class) {
            vertx.eventBus().consumer(annotation.value(), event ->
                invoke(o, m, adjustMessage(parameterType, event))
            );
        } else {
            vertx.eventBus().consumer(annotation.value()).bodyStream()
                    .handler(message -> invoke(o, m, message))
                    .exceptionHandler(t -> LOG.error(t));
        }

    }

    /**
     * wrap message for rxfied interfaces
     */
    private Object adjustMessage(Class forClazz, Message arg) {
        if (forClazz == Message.class) {
            return arg;
        }
        if (forClazz == io.vertx.reactivex.core.eventbus.Message.class) {
            return new io.vertx.reactivex.core.eventbus.Message(arg);
        }
        if (forClazz == io.vertx.rxjava.core.eventbus.Message.class) {
            return new io.vertx.reactivex.core.eventbus.Message<>(arg);
        }
        return null;
    }

    private void invoke(Object o, Method method, Object arg) {
        method.setAccessible(true);
        try {
            method.invoke(o, arg);
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOG.error(e);
        }
    }
}
