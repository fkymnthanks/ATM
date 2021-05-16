package com.atm.rd.server.controller;

import com.atm.rd.common.util.RandomIdHandler;
import com.atm.rd.model.Request;
import com.atm.rd.model.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Server端请求接收入口
 */
public class RequestController {
    private static final Logger logger = LoggerFactory.getLogger(RequestController.class);

    private DispatchController dispatchController = null;

    private RandomIdHandler randomIdGenerator;

    public RequestController() {
        init();
    }

    private void init() {
        this.dispatchController = new DispatchController();
        this.randomIdGenerator = new RandomIdHandler();
    }


    public Response readRequest(Request request) {
        String randomId = randomIdGenerator.getRandomId();
        logger.info("[RandomId]: " + randomId + ";[Tag]: ");
        Response ret = null;
        int type = request.getType();
        if(type>0 && type<9) {
            ret = this.dispatchController.submitRequest(request);
        }else {
            ret = new Response();
            ret.setType(0);
            ret.setCodeState(400);
            ret.setContent("Can't solve this request".getBytes());
        }
        logger.info("[RandomId]: " + randomId + ";[Tag]: ");
        return ret;
    }
}
