package com.atm.rd.client;

import com.atm.rd.common.util.RandomIdGenerator;
import com.atm.rd.model.Request;
import com.atm.rd.model.Response;
import com.atm.rd.server.controller.RequestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 客户端代理，与Server进行交互
 */
public class ClientCnxn {
    private static final Logger logger = LoggerFactory.getLogger(ClientCnxn.class);

    private RequestController requestController = null;

    public ClientCnxn() {
        init();
    }

    private void init() {
        this.requestController = new RequestController();
    }

    /**
     * 发送不带内容的消息
     * @param type
     * @return
     */
    public String send(int type) {
        return String.valueOf(send(type, "").getContent());
    }

    /**
     * 发送带内容的消息
     * @param type
     * @param content
     * @return
     */
    public Response send(int type, String content) {
        String randomId = RandomIdGenerator.getRandomId();
        logger.info("[RandomId]: " + randomId + ";[Tag]: ");
        Response response = null;
        Request request = new Request(type, content.getBytes());
        response = requestController.readRequest(request);
        logger.info("[RandomId]: " + randomId + ";[Tag]: ");
        return response;
    }
}
