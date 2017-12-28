package com.github.kubacech.vertx.eventbus.annotation;

import com.github.kubacech.vertx.eventbus.AnnotatedAbstractVerticle;
import com.github.kubacech.vertx.eventbus.JacksonMessageCodec;
import com.github.kubacech.vertx.eventbus.TypedEventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.reactivex.core.Vertx;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(VertxUnitRunner.class)
public class EventAnnotationProcessorTest {

    private Vertx vertx;

    private static final String MESSAGE_ADDRESS = "address";
    private static final String JSON_ADDRESS = "json.address";
    private static final String RX_ADDRESS = "rx.address";
    private static final String REACTIVEX_ADDRESS = "reactive.address";
    private static final String CUSTOM_KUBACECH_ADDRESS = "kubacech.address";

    private TypedEventBus typedEventBus;

    @Before
    public void setUp() throws Exception {
        this.vertx = Vertx.vertx();
        vertx.eventBus().getDelegate().registerCodec(new JacksonMessageCodec());
        this.typedEventBus = new TypedEventBus(vertx);
    }

    @Test
    public void messageWithJsonPayload(TestContext context) {
        Async async = context.async();

        vertx.getDelegate().deployVerticle(new TestAnnotatedVerticle(async), deployId -> {
            vertx.eventBus().send(MESSAGE_ADDRESS, new JsonObject());
        });
    }

    @Test
    public void justJsonPayload(TestContext context) {
        Async async = context.async();

        vertx.getDelegate().deployVerticle(new TestAnnotatedVerticle(async), deployId -> {
            vertx.eventBus().send(JSON_ADDRESS, new JsonObject());
        });
    }

    @Test
    public void rxMessageWithJsonPayload(TestContext context) {
        Async async = context.async();

        vertx.getDelegate().deployVerticle(new TestAnnotatedVerticle(async), deployId -> {
            vertx.eventBus().send(RX_ADDRESS, new JsonObject());
        });
    }

    @Test
    public void reactivexMessageWithJsonPayload(TestContext context) {
        Async async = context.async();

        vertx.getDelegate().deployVerticle(new TestAnnotatedVerticle(async), deployId -> {
            vertx.eventBus().send(REACTIVEX_ADDRESS, new JsonObject());
        });
    }

    @Test
    public void customKubacechMessageWithJsonPayload(TestContext context) {
        Async async = context.async();

        vertx.getDelegate().deployVerticle(new TestAnnotatedVerticle(async), deployId -> {
            vertx.eventBus().send(CUSTOM_KUBACECH_ADDRESS, new JsonObject());
        });
    }

    @Test
    public void eventAnnotationSpecified(TestContext context) {
        Async async = context.async();

        vertx.getDelegate().deployVerticle(new TestAnnotatedVerticle(async), deployId -> {
            typedEventBus.send(new TestMessageType1());
        });
    }

    @Test
    public void eventAnnotationSpecifiedMessageParametrized(TestContext context) {
        Async async = context.async();

        vertx.getDelegate().deployVerticle(new TestAnnotatedVerticle(async), deployId -> {
            typedEventBus.send(new TestMessageType2());
        });
    }

    @Test
    public void eventAnnotationAuto(TestContext context) {
        Async async = context.async();

        vertx.getDelegate().deployVerticle(new TestAnnotatedVerticle(async), deployId -> {
            typedEventBus.send(new TestMessageType3());
        });
    }

    @Test
    public void eventAnnotationAutoParametrizedMessage(TestContext context) {
        Async async = context.async();

        vertx.getDelegate().deployVerticle(new TestAnnotatedVerticle(async), deployId -> {
            typedEventBus.send(new TestMessageType4());
        });
    }

    class TestAnnotatedVerticle extends AnnotatedAbstractVerticle {

        private Async async;
        public TestAnnotatedVerticle(Async async) {
            this.async = async;
        }

        @EventConsumer(MESSAGE_ADDRESS)
        public void consumeEvent(Message<JsonObject> message) {
            async.complete();
        }
        @EventConsumer(RX_ADDRESS)
        public void consumeEvent(io.vertx.rxjava.core.eventbus.Message<JsonObject> message) {
            async.complete();
        }
        @EventConsumer(REACTIVEX_ADDRESS)
        public void consumeEvent(io.vertx.reactivex.core.eventbus.Message<JsonObject> message) {
            async.complete();
        }
        @EventConsumer(CUSTOM_KUBACECH_ADDRESS)
        public void consumeEvent(com.github.kubacech.vertx.eventbus.Message<JsonObject> message) {
            async.complete();
        }
        @EventConsumer(JSON_ADDRESS)
        public void consumeEvent(JsonObject message) {
            async.complete();
        }
        @Event(TestMessageType1.class)
        public void consume(TestMessageType1 message) {
            async.complete();
        }
        @Event(TestMessageType2.class)
        public void consumem2(Message<TestMessageType2> message) {
            async.complete();
        }
        @Event
        public void consumem3(TestMessageType3 message) {
            async.complete();
        }
        @Event
        public void consumem4(Message<TestMessageType4> message) {
            assertEquals(message.address(), TestMessageType4.class.getName());
            async.complete();
        }
    }

    class TestMessageType1 { }
    class TestMessageType2 { }
    class TestMessageType3 { }
    class TestMessageType4 { }
}