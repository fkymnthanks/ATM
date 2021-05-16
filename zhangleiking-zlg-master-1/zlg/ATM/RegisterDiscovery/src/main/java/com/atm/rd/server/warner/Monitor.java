package com.atm.rd.server.warner;

import com.atm.rd.common.util.RandomIdHandler;
import com.atm.rd.server.service.FaultService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 监控处理机制
 */
public class Monitor {
    private static final Logger logger = LoggerFactory.getLogger(Monitor.class);

    private FaultService exceptionHandler = null;

    private RandomIdHandler randomIdGenerator;

    public Monitor() {
        init();
    }

    private void init() {
        this.exceptionHandler = new FaultService();
        this.randomIdGenerator = new RandomIdHandler();
    }

    public FaultService getExceptionHandler() {
        return exceptionHandler;
    }

    /**
     * 监控机制向异常处理机制通报异常信息
     * @param content
     * @return
     */
    public boolean notifyException(String content) {
        //System.out.println("---Exception Happend---");
        String randomId = randomIdGenerator.getRandomId();
        logger.info("[RandomId]: " + randomId + ";[Tag]: ");
        boolean ret = true;
        //进一步核查request内容中出现的问题，并交由异常处理机制解决
        if(content.contains("error") || content.contains("exception")) {
            ret = this.exceptionHandler.handleException(content);
        }
        logger.info("[RandomId]: " + randomId + ";[Tag]: ");
        return ret;
    }
}
