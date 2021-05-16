package com.atm.rd.server.handler;

import com.atm.rd.common.util.RandomIdHandler;
import com.atm.rd.model.Request;
import com.atm.rd.model.Response;
import com.atm.rd.server.service.FaultService;
import com.atm.rd.server.warner.Monitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * “责任链”模式中，最终处理
 */
public class FinalRequestProcessor {
    private static final Logger logger = LoggerFactory.getLogger(FinalRequestProcessor.class);

    private Monitor monitor = null;

    private RandomIdHandler randomIdGenerator;

    private FaultService exceptionHandler = new FaultService();

    public FinalRequestProcessor() {
        this.monitor = new Monitor();
        this.randomIdGenerator = new RandomIdHandler();
    }

    /**
     * 根据消息类型，具体处理消息
     * @param request
     * @return
     */
    public Response doRequest(Request request) {
        String randomId = randomIdGenerator.getRandomId();
        logger.info("[RandomId]: " + randomId + ";[Tag]: ");
        Response ret = null;
        int type = request.getType();
        switch (type) {
            /**
             * type为1，处理Ping信息
             */
            case 1:
                ret = handlePing(request.getContent());
                break;
            case 2:
                break;
        }
        ret.setType(type);
        logger.info("[RandomId]: " + randomId + ";[Tag]: ");
        return ret;
    }

    /**
     * 处理Ping消息
     * @param contentBytes
     * @return
     */
    private Response handlePing(byte[] contentBytes) {

        Response ret = new Response();
        String content = new String(contentBytes);
        content = content.toLowerCase();
        //this.monitor.getExceptionHandler().handleException("exception");
        if(content.contains("error") || content.contains("exception") || content.contains("warn")) {
            /**
             * @Author Leo
             * @Description //TODO
             * 我们在这里，没有notifyException()而是直接将处理的布尔值返回
             * 来模拟没有触发notifyException()就触发了handleException()的开发错误
            **/
            boolean needFix = exceptionHandler.handleException(content);
            if (needFix) {
                ret.setContent("Exist exception or error".getBytes());
                ret.setCodeState(400);
            }
        } else {
            ret.setContent("Receive Ping succeess! Handle Success!".getBytes());
            ret.setCodeState(200);
        }
        return ret;
    }
}
