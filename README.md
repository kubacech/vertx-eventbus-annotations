# vertx-eventbus-utils
Simple utility classes for simple use of vertx eventbus.
Supports vertx rx interfaces as well (both rxJava1 and rxJava2). 

### Use
Firstly extend  `AnnotatedAbstractVerticle` class or use direcly in your verticle as:
```java
new EventAnnotationProcessor(vertx).process(this);
```

### TypedEventBus
Eventbus wrapper which allows you to send your custom objects through vertx eventbus.
It simply uses codec that encodes and decodes your object with Jackson - so just be sure your objects can be 
serialized and deserialized with Jackson.  
Before you use this register `JacksonMessageCodec` on your eventbus. for example:

```java
vertx.eventBus().registerCodec(new JacksonMessageCodec());
```

If you use `AnnotatedAbstractVerticle` just use `eventBus()` method for dealing with eventbus

```java
//somewhere in your verticle
eventBus().send(new MySpecialEvent());
```
Or use it directly. `TypedEventBus` is just wrapper, so you can create it yourself:
```java
new TypedEventBus(vertx);
```

You can use `publish`, `send` (you know these) and `askFor` methods. Use `askFor` if you care about
return object, but not Message object. Otherwise works same as `send`.

```java
Single<ResponseType> = eventBus().askFor("address", new JsonObject());
  
//or use your type as your event body
Single<ResponseType> = eventBus().askFor("address", new ParameterType());
  
//or again use directly event
Single<ResponseType> = eventBus().askFor(new ParameterType());
```

### Message
If you are replying on eventBus and you want to return your type than use
 `com.github.kubacech.vertx.eventbus.Message` class as a wrapper of `Message`
 
 ```java
//wrap message and reply
new com.github.kubacech.vertx.eventbus.Message(message).reply(yourObj);
  
//or define it as method argument
@Event
private void handle(Message<MyMessage> message) {
    message.reply(new TypedResponse());
}
 ```

### @Event
For consuming typed events. Using `Class` object as address name

```java
@Event(MyMessage.class)
private void handle(MyMessage message) {
    // do something with your message
}
  
//or simply 
@Event
private void handle(MyMessage message) {
    //(method argument class is used) 
    // do something with your message
}
  
//or even
@Event
private void handle(Message<MyMessage> message) {
    // (method argument's parametrized type is used - MyMessage) 
    // do something with your message
}
```


### @EventConsumer
Allows consume events based on annotation definition. It uses *String* address passed to it.  


```java
@EventConsumer("message.address")
private void handle(Message<JsonObject> message) {
    // do something with your message
}
 
// or skip Message and use message body directly
 
@EventConsumer("message.address")
private void handle(JsonObject message) {
    // do something with your message
}
```

