package com.openmhealth.client;

import com.openmhealth.common.model.Action;
import com.openmhealth.common.model.Request;
import com.openmhealth.common.model.Response;
import com.openmhealth.common.idal.CommonController;
import com.openmhealth.common.util.RandomIdGenerator;
import com.openmhealth.resource.controller.AccessController;
import com.openmhealth.server.controller.RequestHandleController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 客户端入口
 */
public class Client {

    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    private CommonController commonController;

    private AccessController accessController;

    private void init() {
        this.commonController = new RequestHandleController();
        this.accessController = new AccessController();
    }

    public Client() {
        init();
    }

    /**
     * 客户端获取资源的方法
     * @param request
     * @return
     */
    public String requestAccess(Request request) {
        String randomId = RandomIdGenerator.getRandomId();
        logger.info("[RandomId]: " + randomId + ";[Tag]: " + "D");
        String ret;
        Response authorization = this.commonController.handleRequest(request);
        if(authorization.getType() == request.getType() && authorization.getCodeState()==200) {
            request.setAction(Action.ACCESS);
            ret = accessResource(request);
        }else {
            ret = null;
        }
        logger.info("[RandomId]: " + randomId + ";[Tag]: " + "D");
        return ret;
    }

    /**
     * 如果客户端通过验证，则获取资源
     * @param request
     * @return
     */
    private String accessResource(Request request) {
        String randomId = RandomIdGenerator.getRandomId();
        logger.info("[RandomId]: " + randomId + ";[Tag]: ");
        Response ret = null;
        ret = this.accessController.handleRequest(request);
        logger.info("[RandomId]: " + randomId + ";[Tag]: ");
        return new String(ret.getContent());
    }

    public static void main(String[] args) {
        Client client = new Client();
        Request request = new Request(1, Action.AUTHORIZE, "haha yes".getBytes());
        client.requestAccess(request);
    }
}
