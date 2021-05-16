package com.atm.rd.cli;

import com.atm.rd.common.util.RandomIdHandler;
import com.atm.rd.model.Request;
import com.atm.rd.model.Response;
import com.atm.rd.server.controller.RequestController;
import com.atm.rd.server.handler.FinalRequestProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 客户端
 */
public class Client {

    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    private Cnxn clientCnxn = null;

    private RandomIdHandler randomIdGenerator;

    private FinalRequestProcessor finalRequestProcessor;

    public Client() {
        init();
    }

    private void init() {
        this.clientCnxn = new Cnxn();
        this.randomIdGenerator = new RandomIdHandler();
        /**
         * @Author Leo
         * @Description //TODO
         * 为了在构造错误二，我们在init()方法中，初始化Echo()方法的触发类
         *并在Ping()方法调用前，调用一次Echo()
        **/
        this.finalRequestProcessor = new FinalRequestProcessor();
    }

    /**
     * 发送Ping消息
     * @return
     */
    private String sendPing() {
        return this.clientCnxn.send(1);
    }

    /**
     * 发送携带内容的Ping消息
     * @param content
     * @return
     */
    private Response sendPing(String content) {
        String randomId = randomIdGenerator.getRandomId();
        logger.info("[RandomId]: " + randomId + ";[Tag]: " + "D");
        Response ret = this.clientCnxn.send(1, content);
        logger.info("[RandomId]: " + randomId + ";[Tag]: " + "D");
        return ret;
    }

    class inner{
        public void test() {

        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        RequestController requestController = new RequestController();
        requestController.readRequest(new Request());

//        try {
//            Class cls = Class.forName("com.atm.rd.client.Client$inner");
//            Method[] methods = cls.getDeclaredMethods();
//            Class[] classes = cls.getDeclaredClasses();
//            for(Method method : methods) {
//                System.out.println(method.getName());
//            }
//            for(Class clazz : classes) {
//                System.out.println(clazz.getName());
//            }
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
        for(int i=0; i<2; i++) {
            client.finalRequestProcessor.doRequest(new Request(1,"ECHOMSG".getBytes()));
            Response response = client.sendPing("PINGMSG");
        }
    }
}
