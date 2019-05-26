package ru.chufeng.plsqllang.server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static java.sql.DriverManager.getConnection;

public class ConnectionSingleton {
    private static volatile ConnectionSingleton instance;
    private String user;
    private String pass;
    private String dbUrl;

    public static ConnectionSingleton getInstance(String connectionString) {
        ConnectionSingleton localInstance = instance;
        if (localInstance == null) {
            synchronized (ConnectionSingleton.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new ConnectionSingleton(connectionString);
                }
            }
        }
        return localInstance;
    }

    private ConnectionSingleton(String connectionString) {
        user = connectionString.substring(0, connectionString.indexOf("/"));
        pass = connectionString.substring(connectionString.indexOf("/") + 1, connectionString.indexOf("@"));
        dbUrl = "jdbc:oracle:thin:" + connectionString.substring(connectionString.indexOf("@")).replace("/", ":");
    }

    public Connection get() throws SQLException {
        return getConnection(dbUrl, user, pass);
    }
}
