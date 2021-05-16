package com.openmhealth.util.serialize;

import com.openmhealth.util.property.PropertyUtil;

/**
 *
 */
public class SerializerInstance implements Serializer {

    private String serializeType;

    private Serializer serializer;

    private SerializerInstance() {
        this.serializeType = PropertyUtil.getProperty("serializer.type");
        if(this.serializeType.equals("jdk")) {
            serializer = new JdkSerializer();
        }
    }

    @Override
    public byte[] serialize(Object obj) {
        return serializer.serialize(obj);
    }

    @Override
    public Object deserialize(byte[] data) {
        return serializer.deserialize(data);
    }

    @Override
    public Object deserialize(byte[] data, int off) {
        return serializer.deserialize(data, off);
    }

    private static class InnerSerializerInstance {
        private static final SerializerInstance INSTANCE = new SerializerInstance();
    }

    public static SerializerInstance getInstance() {
        return InnerSerializerInstance.INSTANCE;
    }
}
