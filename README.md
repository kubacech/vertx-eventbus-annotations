# vertx-eventbus-annotations
Simple utility which allows consume events based on annotation definition.
Supports vertx rx interfaces as well (both rxJava1 and rxJava2).

### Use
Firstly extend  `AnnotatedAbstractVerticle` class or use direcly in your verticle as:
```java
new EventAnnotationProcessor(vertx).process(this);
```
if you extend `AnnotatedAbstractVerticle` class, then make sure you will call `super.start()`
in your overriden `start` method .

then annotate your event handling methods:

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