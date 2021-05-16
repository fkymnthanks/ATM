package com.openmhealth.server.controller;

import com.openmhealth.authorization.controller.UserAuthorizeController;
import com.openmhealth.common.model.Action;
import com.openmhealth.common.model.Request;
import com.openmhealth.common.model.Response;
import com.openmhealth.common.idal.CommonController;
import com.openmhealth.common.util.RandomIdGenerator;
import com.openmhealth.resource.controller.AccessController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 请求处理Controller
 */
public class RequestHandleController implements CommonController{

    private static final Logger logger = LoggerFactory.getLogger(RequestHandleController.class);

    private CommonController handleController;

    public RequestHandleController() {

    }

    private Response dispatchRequest(Request request) {
        String randomId = RandomIdGenerator.getRandomId();
        logger.info("[RandomId]: " + randomId + ";[Tag]: ");
        Response ret = null;
        int type = request.getType();
        switch (type) {
            case 1:
            case 2:
            case 3:
                if(request.getAction() == Action.AUTHORIZE) {
                    this.handleController = new UserAuthorizeController();
                }else if(request.getAction() == Action.ACCESS) {
                    this.handleController = new AccessController();
                }
                ret = this.handleController.handleRequest(request);
                break;
            default:
                break;
        }
        logger.info("[RandomId]: " + randomId + ";[Tag]: ");
        return ret;
    }

    /**
     * 接收请求，并决定是否进行派遣处理
     * @param request
     * @return
     */
    public Response handleRequest(Request request) {
        String randomId = RandomIdGenerator.getRandomId();
        logger.info("[RandomId]: " + randomId + ";[Tag]: ");
        int type = request.getType();
        Response ret = null;
        if(type>=1 && type<=9) {
            ret = dispatchRequest(request);
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
