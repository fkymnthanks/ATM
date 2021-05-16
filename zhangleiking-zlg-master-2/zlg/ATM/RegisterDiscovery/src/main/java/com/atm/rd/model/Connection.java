package com.atm.rd.model;

import lombok.Getter;
import lombok.Setter;

/**
 * 数据库连接
 */
public class Connection {

    @Getter @Setter
    private String ip;

    @Getter @Setter
    private int port;

    public Connection(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }
}
