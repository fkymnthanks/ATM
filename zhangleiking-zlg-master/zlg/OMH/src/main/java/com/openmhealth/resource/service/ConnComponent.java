package com.openmhealth.resource.service;

import com.openmhealth.common.util.RandomIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 连接资源服务器
 */
public class ConnComponent {

    private static final Logger logger = LoggerFactory.getLogger(ConnComponent.class);

    private ResourceServer resourceServer;

    private void init() {
        this.resourceServer = new ResourceServer();
    }

    public ConnComponent() {
        init();
    }

    public String getData() {
        String randomId = RandomIdGenerator.getRandomId();
        logger.info("[RandomId]: " + randomId + ";[Tag]: ");
        String resource = this.resourceServer.produceResource();
        logger.info("[RandomId]: " + randomId + ";[Tag]: ");
        return  resource;
    }
}
