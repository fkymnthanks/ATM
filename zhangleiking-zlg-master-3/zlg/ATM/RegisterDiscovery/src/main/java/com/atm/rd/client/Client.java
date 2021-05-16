package com.atm.rd.client;

import com.atm.rd.common.util.RandomIdGenerator;
import com.atm.rd.model.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * 客户端
 */
public class Client {

    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    private ClientCnxn clientCnxn = null;

    public Client() {
        init();
        inner ii = new inner();
        System.out.println(ii.getClass().getName());
    }

    private void init() {
        this.clientCnxn = new ClientCnxn();
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
        String randomId = RandomIdGenerator.getRandomId();
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
        try {
            Class cls = Class.forName("com.atm.rd.client.Client$inner");
            Method[] methods = cls.getDeclaredMethods();
            Class[] classes = cls.getDeclaredClasses();
            for(Method method : methods) {
                System.out.println(method.getName());
            }
            for(Class clazz : classes) {
                System.out.println(clazz.getName());
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        //Response response = client.sendPing("EXCEPTION");
        //System.out.println(response.toString());
        Response response2 = client.sendPing("123");
        //System.out.println(response2.toString());
    }
}
