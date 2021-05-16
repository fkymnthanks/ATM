package com.atm.rd.server.service;

import com.atm.rd.common.util.RandomIdHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 异常处理机制
 */
public class FaultService {
    private static final Logger logger = LoggerFactory.getLogger(FaultService.class);

    private RandomIdHandler randomIdGenerator;

    public FaultService() {
        this.randomIdGenerator = new RandomIdHandler();
    }

    /**
     * 根据报告的具体内容的紧急情况，来决定异常的处理方式
     * @param content
     * @return
     */
    public boolean handleException(String content) {
        String randomId = randomIdGenerator.getRandomId();
        logger.info("[RandomId]: " + randomId + ";[Tag]: ");
        //根据content具体内容，对异常进行解决
        //以下逻辑均是模拟检测
        boolean ret = false;
        if(content.contains("exception") || content.contains("error")) {
            //处理当前的异常与错误，例如可以先记录log等
            ret = true;
        }
        logger.info("[RandomId]: " + randomId + ";[Tag]: ");
        return ret;
    }
}
