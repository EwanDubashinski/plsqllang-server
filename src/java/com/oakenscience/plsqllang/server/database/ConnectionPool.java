package com.oakenscience.plsqllang.server.database;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class ConnectionPool {
    private static volatile ConnectionPool instance;
    private Map<String, OraConnection> connections = new HashMap<>();

    public static ConnectionPool getInstance() {
        ConnectionPool localInstance = instance;
        if (localInstance == null) {
            synchronized (ConnectionPool.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new ConnectionPool();
                }
            }
        }
        return localInstance;
    }


    public Connection get(String connectionString) {
        if (connections.containsKey(connectionString)) {
            return connections.get(connectionString).getConn();
        } else {
            OraConnection oraConn = new OraConnection(connectionString);
            connections.put(connectionString, oraConn);
            return oraConn.getConn();
        }
    }
}
