package com.atm.rd.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

/**
 * 响应请求实体模型
 */
public class Response {


    /**
     * 0: 无法处理的请求返回
     * 1: PingEcho请求返回
     * -1: 异常或Error返回
     */
    @Getter @Setter
    private int type;

    /**
     * 状态码
     * 200 表示执行成功
     * 400 表示存在异常
     */
    @Getter @Setter
    private int codeState;

    @Getter @Setter
    private byte[] content;

    public Response() {

    }

    public Response(int type, byte[] content) {
        this.type = type;
        this.content = content;
    }

    @Override
    public String toString() {
        return "Response{" +
                "type=" + type +
                ", codeState=" + codeState +
                ", content=" + new String(content) +
                '}';
    }
}
