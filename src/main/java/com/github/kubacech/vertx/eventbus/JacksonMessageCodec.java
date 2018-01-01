package com.github.kubacech.vertx.eventbus;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.Json;

public class JacksonMessageCodec implements MessageCodec<Object, Object> {

    @Override
    public void encodeToWire(Buffer buffer, Object o) {
        //buffer = classNameLength(4B),jsonLength(4B), classname, json
        String json = Json.encode(o);
        String className = o.getClass().getName();
        buffer.appendInt(o.getClass().getName().getBytes().length);
        buffer.appendInt(json.getBytes().length);
        buffer.appendString(className);
        buffer.appendString(json);
    }

    @Override
    public Object decodeFromWire(int pos, Buffer buffer) {
        int _pos = pos;

        int classNameLength = buffer.getInt(_pos);
        int jsonLength = buffer.getInt(_pos+=4);

        String className = buffer.getString(_pos+=4, _pos+=classNameLength);
        String json = buffer.getString(_pos, _pos+=jsonLength);

        Class classType;
        try {
            classType = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        Object o = Json.decodeValue(json, classType);
        return Json.decodeValue(json, classType);
    }

    @Override
    public Object transform(Object o) {
        return o;
    }

    @Override
    public String name() {
        return this.getClass().getName();
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }
}
