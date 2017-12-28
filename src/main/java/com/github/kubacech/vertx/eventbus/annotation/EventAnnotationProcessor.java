package com.github.kubacech.vertx.eventbus.annotation;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EventAnnotationProcessor {

    Logger LOG = LoggerFactory.getLogger(EventAnnotationProcessor.class);

    protected Vertx vertx;

    private static List<Class> messageTypes = new ArrayList<>();
    static {
        messageTypes.add(Message.class);
        messageTypes.add(io.vertx.rxjava.core.eventbus.Message.class);
        messageTypes.add(io.vertx.reactivex.core.eventbus.Message.class);
        messageTypes.add(com.github.kubacech.vertx.eventbus.Message.class);
    }

    public EventAnnotationProcessor(Vertx vertx) {
        this.vertx = vertx;
    }

    public void process(Object object) {
        Class clazz = object.getClass();

        Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(EventConsumer.class) || method.isAnnotationPresent(Event.class))
                .forEach(method -> initEventConsumer(object, method));
    }

    private void initEventConsumer(Object o, Method m) {
        if (m.getParameterCount() != 1) {
            throw new IllegalArgumentException("Method annotated with " + EventConsumer.class.getName() + " must have one parameter");
        }

        Class parameterType = m.getParameters()[0].getType();
        String eventAddress = resolveEventAddress(m);

        if (parameterType == Message.class || parameterType == io.vertx.rxjava.core.eventbus.Message.class
                || parameterType == io.vertx.reactivex.core.eventbus.Message.class
                || parameterType == com.github.kubacech.vertx.eventbus.Message.class) {
            vertx.eventBus().consumer(eventAddress, event ->
                invoke(o, m, adjustMessage(parameterType, event))
            );
        } else {
            vertx.eventBus().consumer(eventAddress).bodyStream()
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
            return new io.vertx.rxjava.core.eventbus.Message<>(arg);
        }
        if (forClazz == com.github.kubacech.vertx.eventbus.Message.class) {
            return new com.github.kubacech.vertx.eventbus.Message(arg);
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

    private String resolveEventAddress(Method m) {
        if (m.isAnnotationPresent(EventConsumer.class)) {
            return m.getAnnotation(EventConsumer.class).value();
        }
        if (m.isAnnotationPresent(Event.class)) {
            Class c =  m.getAnnotation(Event.class).value();
            if (c == Void.class) {
                Parameter p = m.getParameters()[0];
                c =  p.getType();
                if (messageTypes.contains(p.getType())) {
                    ParameterizedType pt = ((ParameterizedType)p.getParameterizedType());
                    c = (Class) pt.getActualTypeArguments()[0];
                }
            }
            return c.getName();
        }
        throw new IllegalArgumentException("No annotation found for method " + m.getName());
    }
}
