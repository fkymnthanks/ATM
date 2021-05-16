package com.atm.rd.server.handler;

import com.atm.rd.common.util.RandomIdHandler;
import com.atm.rd.model.Request;
import com.atm.rd.model.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * “责任链”模式中，预处理
 */
public class PrepRequestProcessor {
    private static final Logger logger = LoggerFactory.getLogger(PrepRequestProcessor.class);

    private FinalRequestProcessor finalRequestProcessor = null;

    private RandomIdHandler randomIdGenerator;

    public PrepRequestProcessor() {
        init();
    }

    private void init() {
        this.finalRequestProcessor = new FinalRequestProcessor();
        this.randomIdGenerator = new RandomIdHandler();
    }

    /**
     * 在“当前责任链”环节即可解决
     * @param request
     * @return
     */
    public Response pRequest(Request request) {
        String randomId = randomIdGenerator.getRandomId();
        logger.info(";[RandomId]: " + randomId + ";[Tag]: ");
        Response ret = pRequest(request, false);
        logger.info(";[RandomId]: " + randomId + ";[Tag]: ");
        return ret;
    }

    /**
     * 向“后续责任链”环节传递
     * @param request
     * @param backPass
     * @return
     */
    public Response pRequest(Request request, boolean backPass) {
        String randomId = randomIdGenerator.getRandomId();
        logger.info("[RandomId]: " + randomId + ";[Tag]: ");
        Response ret = null;
        if(backPass) {
            ret = finalRequestProcessor.doRequest(request);
        } else {
            int type = request.getType();
            switch (type) {
                //直接根据type在当前处理了
                //doSomething
                default:
                    break;
            }
            ret = new Response();
            ret.setType(type);
        }
        logger.info("[RandomId]: " + randomId + ";[Tag]: ");
        return ret;
    }
}
