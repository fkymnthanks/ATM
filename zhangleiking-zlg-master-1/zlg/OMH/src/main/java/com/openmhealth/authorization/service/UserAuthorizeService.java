package com.openmhealth.authorization.service;

import com.openmhealth.common.model.Request;
import com.openmhealth.common.model.Response;
import com.openmhealth.common.util.RandomIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用户权限控制Service
 */
public class UserAuthorizeService {
    private static final Logger logger = LoggerFactory.getLogger(UserAuthorizeService.class);

    /**
     * 返回验证结果
     * @param request
     * @param permission
     * @return
     */
    public Response authorize(Request request, boolean permission) {
        String randomId = RandomIdGenerator.getRandomId();
        logger.info("[RandomId]: " + randomId + ";[Tag]: D");
        Response ret = new Response();
        ret.setType(request.getType());
        if(permission) {
            ret.setCodeState(200);
            ret.setContent("YES".getBytes());
        } else {
            ret.setCodeState(400);
            ret.setContent("NO".getBytes());
        }
        logger.info("[RandomId]: " + randomId + ";[Tag]: D");
        return ret;
    }
}
