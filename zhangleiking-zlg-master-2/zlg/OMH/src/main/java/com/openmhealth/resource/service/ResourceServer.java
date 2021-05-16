package com.openmhealth.resource.service;

import com.openmhealth.common.util.RandomIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 作为资源服务器，用于产生资源
 */
public class ResourceServer {

    private static final Logger logger = LoggerFactory.getLogger(ResourceServer.class);

    /**
     * 产生资源
     * @return
     */
    public String produceResource() {
        String randomId = RandomIdGenerator.getRandomId();
        logger.info("[RandomId]: " + randomId + ";[Tag]: ");
        String content = "This is resource";
        logger.info("[RandomId]: " + randomId + ";[Tag]: ");
        return content;
    }
}
