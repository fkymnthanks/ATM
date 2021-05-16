package com.openmhealth.resource.controller;

import com.openmhealth.common.idal.CommonController;
import com.openmhealth.common.model.Request;
import com.openmhealth.common.model.Response;
import com.openmhealth.common.util.RandomIdGenerator;
import com.openmhealth.resource.service.ConnComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 连接并获取资源的controller
 */
public class AccessController implements CommonController {

    private static final Logger logger = LoggerFactory.getLogger(AccessController.class);

    private ConnComponent connComponent;

    private void init() {
        this.connComponent = new ConnComponent();
    }

    public AccessController() {
        init();
    }

    public Response handleRequest(Request request) {
        String randomId = RandomIdGenerator.getRandomId();
        logger.info("[RandomId]: " + randomId + ";[Tag]: ");
        Response ret = new Response();
        ret.setType(request.getType());
        ret.setCodeState(200);
        ret.setContent(this.connComponent.getData().getBytes());
        logger.info("[RandomId]: " + randomId + ";[Tag]: ");
        return ret;
    }
}
