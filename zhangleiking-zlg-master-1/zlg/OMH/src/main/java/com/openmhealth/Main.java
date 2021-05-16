package com.openmhealth;

import com.openmhealth.client.Client;
import com.openmhealth.common.model.Action;
import com.openmhealth.common.model.Request;

/**
 * 主入口
 */
public class Main {

    public static void main(String[] args) {
        Client client = new Client();
        for(int i=0; i<2; i++) {
            Request request = new Request(1, Action.AUTHORIZE, "haha yes".getBytes());
            client.requestAccess(request);
        }
    }
}
