package com.openmhealth.common.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

/**
 * 消息请求实体模型
 */
public class Request {

    /**
     * 正常请求type为1-9
     */
    @Getter @Setter
    private int type;

    @Getter @Setter
    private Action action;

    @Getter @Setter
    private byte[] content;

    public Request() {

    }

    public Request(int type, byte[] content) {
        this.type = type;
        this.content = content;
    }

    public Request(int type, Action action, byte[] content) {
        this.type = type;
        this.action = action;
        this.content = content;
    }

    @Override
    public String toString() {
        return "Request{" +
                "type=" + type +
                ", content=" + Arrays.toString(content) +
                '}';
    }
}
