package com.atm.rd.server.repository;

import com.atm.rd.model.Connection;

/**
 * 数据库连接
 */
public class ServerRepository {

    private ConnectionPool connectionPool;

    private String jdbc;

    public ServerRepository() {
        connectionPool = new ConnectionPool();
    }

    public void getData() {
        Connection conn = connectionPool.getConnection(this.jdbc);
    }
}
