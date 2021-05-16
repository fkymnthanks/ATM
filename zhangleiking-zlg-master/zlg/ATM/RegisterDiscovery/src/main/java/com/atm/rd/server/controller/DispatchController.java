package com.atm.rd.server.controller;

import com.atm.rd.common.util.RandomIdHandler;
import com.atm.rd.model.Request;
import com.atm.rd.model.Response;
import com.atm.rd.server.handler.PrepRequestProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 根据请求的不同，派给不同的Processor进行处理
 */
public class DispatchController {
    private static final Logger logger = LoggerFactory.getLogger(DispatchController.class);

    private PrepRequestProcessor prepRequestProcessor = null;

    private RandomIdHandler randomIdGenerator;

    public DispatchController() {
        init();
    }

    private void init() {
        this.prepRequestProcessor = new PrepRequestProcessor();
        this.randomIdGenerator = new RandomIdHandler();
    }

    public Response submitRequest(Request request) {
        String randomId = randomIdGenerator.getRandomId();
        logger.info("[RandomId]: " + randomId + ";[Tag]: ");
        Response ret = null;
        int type = request.getType();
        switch (type) {
            case 1:
            case 2:
            case 3:
                ret = this.prepRequestProcessor.pRequest(request, true);
                break;
            case 4:
            case 5:
            case 6:
                ret = this.prepRequestProcessor.pRequest(request);
                break;
        }
        logger.info("[RandomId]: " + randomId + ";[Tag]: ");
        return ret;
    }
}
