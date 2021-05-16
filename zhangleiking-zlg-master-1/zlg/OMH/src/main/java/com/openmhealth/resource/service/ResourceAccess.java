package com.openmhealth.resource.service;

import com.openmhealth.common.util.RandomIdGenerator;
import com.openmhealth.resource.controller.AccessController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 连接资源服务器
 */
public class ResourceAccess {

    private static final Logger logger = LoggerFactory.getLogger(ResourceAccess.class);

    private ResourceServer resourceServer;

    private void init() {
        this.resourceServer = new ResourceServer();
    }

    public ResourceAccess() {
        init();
    }

    public String getResource() {
        String randomId = RandomIdGenerator.getRandomId();
        logger.info("[RandomId]: " + randomId + ";[Tag]: ");
        String resource = this.resourceServer.produceResource();
        logger.info("[RandomId]: " + randomId + ";[Tag]: ");
        return  resource;
    }
}
