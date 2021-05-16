package com.atm.rd.server.handler;

import com.atm.rd.common.util.RandomIdHandler;
import com.atm.rd.model.Request;
import com.atm.rd.model.Response;
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
        /**
         * @Author Leo
         * @Description //TODO
         * 源代码中，只有monitor运行的content发生错误的时候，才会触发notifyException
         * 我们现在模拟代码实现错误的情况，也就是当content未发生错我，我们触发notifyException，否则不触发
        **/
        if(!content.contains("exception")) {
            boolean needFix = this.monitor.notifyException(content);
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
