package com.oakenscience.plsqllang.server.database;

import java.sql.Connection;
import java.sql.SQLException;

import static java.sql.DriverManager.getConnection;


public class OraConnection {
    private String user;
    private String pass;
    private String dbUrlByService;
    private String dbUrlBySID;
    private Connection connection;
    private int errorCode;
    private String errorMessage;
    private boolean isValid = false;


    private void parse(String connectionString) {
        user = connectionString.substring(0, connectionString.indexOf("/"));
        pass = connectionString.substring(connectionString.indexOf("/") + 1, connectionString.indexOf("@"));
        dbUrlByService = "jdbc:oracle:thin:" + connectionString.substring(connectionString.indexOf("@"));
        dbUrlBySID = dbUrlByService.replace("/", ":");
    }

    public OraConnection(String connectionString) {
        parse(connectionString);
        connect();
    }

    private void connect() {
        try {
            connection = getConnection(dbUrlBySID, user, pass);
        } catch (SQLException ex) {
            if (ex.getErrorCode() == 12505) {
                try {
                    connection = getConnection(dbUrlByService, user, pass);
                } catch (SQLException ex2) {
                    setException(ex2.getErrorCode(), ex2.getLocalizedMessage());
                }
            } else {
                setException(ex.getErrorCode(), ex.getLocalizedMessage());
            }
        }
        try {
            isValid = connection.isValid(10);
        } catch (SQLException ex) {
            setException(ex.getErrorCode(), ex.getLocalizedMessage());
        }
    }

    private void setException(int code, String message) {
        errorCode = code;
        errorMessage = message;
        isValid = false;
    }

    public Connection getConn() {
        try {
            isValid = connection.isValid(10);
            if (!isValid) connect();
        } catch (SQLException ex) {
            setException(ex.getErrorCode(), ex.getLocalizedMessage());
        }
        return connection;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isValid() {
        return isValid;
    }

}
