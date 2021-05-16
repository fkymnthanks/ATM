package com.openmhealth.util.serialize;


import com.openmhealth.util.compress.JdkZip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * implement serialization by Jdk's serialization
 *
 * @author Vincent
 */
public class JdkSerializer implements Serializer {

    private final static Logger LOGGER = LoggerFactory.getLogger(JdkSerializer.class);

    /**
     * serialize and compress，to reduce network traffic
     *
     * @param obj
     * @return
     */
    public byte[] serialize(Object obj) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.close();
            byte[] bytes = bos.toByteArray();
            return new JdkZip().zip(bytes);
        } catch (IOException e) {
            LOGGER.error("", e);
        }
        return null;
    }

    /**
     * unzip and then deserialize
     *
     * @param bytes
     * @return
     */
    public Object deserialize(byte[] bytes) {
        byte[] new_bytes = new JdkZip().unzip(bytes);
        ByteArrayInputStream bis = new ByteArrayInputStream(new_bytes);
        try {
            ObjectInputStream ois = new ObjectInputStream(bis);
            return ois.readObject();
        } catch (Exception e) {
            LOGGER.error("", e);
        }
        return null;
    }

    public Object deserialize(byte[] data, int off){
        return null;
    }
}
