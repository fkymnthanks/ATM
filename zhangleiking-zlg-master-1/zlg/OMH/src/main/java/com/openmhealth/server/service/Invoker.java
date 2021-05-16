package com.openmhealth.server.service;

import java.util.Map;

/**
 *
 */
public class Invoker {

    private Map<String, Object> handlerMap;

    public Invoker() {}

    public Invoker(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    public Object invoke(Object invocation) {
        String className = invocation.getClass().getName();
        Object serviceBean = handlerMap.get(className);

        Class<?> serviceClass = serviceBean.getClass();
        String methodName = invocation.getClass().getMethods()[0].getName();
        //set these
        Class<?>[] parameterTypes = null;
        Object[] parameters = null;

        Object response = null;
        //set response
        return response;
    }
}
