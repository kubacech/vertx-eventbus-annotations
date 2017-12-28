package com.github.kubacech.vertx.eventbus;

import com.github.kubacech.vertx.eventbus.annotation.EventConsumer;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.reactivex.core.Vertx;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(VertxUnitRunner.class)
public class TypedEventBusTest {

    private static final String TYPED_ADDRESS = "typed.address";
    private static final String PROJECT_MESSAGE_ADDRESS = "project.message.address";
    private static final String JSON_TYPE_ADDRESS = "json.type.address";

    private static final String PROJECT_MESSAGE_REPLY_ADDRESS = "project.message.reply.address";
    private static final String MESSAGE_REPLY_ADDRESS = "message.reply.address";

    private Vertx vertx;
    private TypedEventBus eventBus;

    @Before
    public void setUp() throws Exception {
        this.vertx = Vertx.vertx();
        vertx.eventBus().getDelegate().registerCodec(new JacksonMessageCodec());
        this.eventBus = new TypedEventBus(vertx);
    }

    @Test
    public void justTypedPayload(TestContext context) {
        Async async = context.async();

        vertx.getDelegate().deployVerticle(new TestAnnotatedVerticle(async), deployId -> {
            eventBus.send(TYPED_ADDRESS, testMessage());
        });
    }

    @Test
    public void messageWithTypedPayload(TestContext context) {
        Async async = context.async();

        vertx.getDelegate().deployVerticle(new TestAnnotatedVerticle(async), deployId -> {
            eventBus.send(PROJECT_MESSAGE_ADDRESS, testMessage());
        });
    }

    @Test
    public void jsonPayload(TestContext context) {
        Async async = context.async();

        vertx.getDelegate().deployVerticle(new TestAnnotatedVerticle(async), deployId -> {
            eventBus.send(JSON_TYPE_ADDRESS, new JsonObject().put("test",123));
        });
    }

    @Test
    public void askForTypedProjectMessage(TestContext context) {
        Async async = context.async();

        TypedMessage tm = testMessage();
        vertx.getDelegate().deployVerticle(new TestAnnotatedVerticle(async), deployId -> {
            eventBus.<TypedMessage>askFor(PROJECT_MESSAGE_REPLY_ADDRESS, tm).subscribe(m ->{
                assertNotNull(m);
                assertNotNull(m.getCode());
                assertEquals(m.getCode(), "reply");
                async.complete();
            });
        });
    }

    @Test
    public void askForTypedMessage(TestContext context) {
        Async async = context.async();

        TypedMessage tm = testMessage();
        vertx.getDelegate().deployVerticle(new TestAnnotatedVerticle(async), deployId -> {
            eventBus.<TypedMessage>askFor(MESSAGE_REPLY_ADDRESS, tm).subscribe(m ->{
                assertNotNull(m);
                assertNotNull(m.getCode());
                assertEquals(m.getCode(), "reply");
                async.complete();
            });
        });
    }

    @Test
    public void askForTypedMessageSingle(TestContext context) {
        Async async = context.async();

        TypedMessage tm = testMessage();
        vertx.getDelegate().deployVerticle(new TestAnnotatedVerticle(async), deployId -> {
            Single<TypedMessage> single = eventBus.askFor(MESSAGE_REPLY_ADDRESS, tm);
            single.subscribe(m ->{
                assertNotNull(m);
                assertNotNull(m.getCode());
                assertEquals(m.getCode(), "reply");
                async.complete();
            });
        });
    }

    private TypedMessage testMessage() {
        return new TypedMessage("test", new BigDecimal("0.005"));
    }

    class TestAnnotatedVerticle extends AnnotatedAbstractVerticle {

        private Async async;
        public TestAnnotatedVerticle(Async async) {
            this.async = async;
        }

        @EventConsumer(PROJECT_MESSAGE_ADDRESS)
        public void consumeEvent(com.github.kubacech.vertx.eventbus.Message<TypedMessage> message) {
            Object body = message.body();
            validate(message.body());
        }
        @EventConsumer(TYPED_ADDRESS)
        public void consumeEvent(TypedMessage message) {
            validate(message);
        }
        @EventConsumer(JSON_TYPE_ADDRESS)
        public void consumeJson(JsonObject json){
            async.complete();
        }

        @EventConsumer(PROJECT_MESSAGE_REPLY_ADDRESS)
        public void consumeEventProjectMessage(com.github.kubacech.vertx.eventbus.Message<TypedMessage> message) {
           message.reply(new TypedMessage("reply", BigDecimal.ONE));
        }
        @EventConsumer(MESSAGE_REPLY_ADDRESS)
        public void consumeEventMessage(io.vertx.core.eventbus.Message<TypedMessage> message) {
            new Message<>(message).reply(new TypedMessage("reply", BigDecimal.ONE));
        }

        private void validate(TypedMessage message) {
            assertNotNull(message);
            assertNotNull(message.getCode());
            assertNotNull(message.getAmount());
            async.complete();
        }
    }

    static class TypedMessage {
        private String code;
        private BigDecimal amount;

        public TypedMessage() {
        }
        public TypedMessage(String code, BigDecimal amount) {
            this.code = code;
            this.amount = amount;
        }
        public String getCode() {
            return code;
        }
        public void setCode(String code) {
            this.code = code;
        }
        public BigDecimal getAmount() {
            return amount;
        }
        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }
    }
}