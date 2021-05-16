package com.openmhealth.authorization.controller;

import com.openmhealth.authorization.service.UserAuthorizeService;
import com.openmhealth.common.model.Request;
import com.openmhealth.common.model.Response;
import com.openmhealth.common.idal.CommonController;
import com.openmhealth.common.util.RandomIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用户权限控制Controller
 */
public class UserAuthorizeController implements CommonController {

    private static final Logger logger = LoggerFactory.getLogger(UserAuthorizeController.class);

    private UserAuthorizeService userAuthorizeService;

    private void init() {
        this.userAuthorizeService = new UserAuthorizeService();
    }

    public UserAuthorizeController() {
        init();
    }

    /**
     * 进行验证
     * @param request
     * @return
     */
    public Response checkPermission(Request request) {
        String randomId = RandomIdGenerator.getRandomId();
        logger.info("[RandomId]: " + randomId + ";[Tag]: D");
        String content = new String(request.getContent()).toLowerCase();
        Response ret = null;
        if(content.contains("yes")) {
            ret = userAuthorizeService.authorize(request, true);
        } else {
            ret = userAuthorizeService.authorize(request, false);
        }
        logger.info("[RandomId]: " + randomId + ";[Tag]: D");
        return ret;
    }

    /**
     * 接收请求
     * @param request
     * @return
     */
    public Response handleRequest(Request request) {
        String randomId = RandomIdGenerator.getRandomId();
        logger.info("[RandomId]: " + randomId + ";[Tag]: ");
        Response ret;
        ret = checkPermission(request);
        logger.info("[RandomId]: " + randomId + ";[Tag]: ");
        return ret;
    }
}
