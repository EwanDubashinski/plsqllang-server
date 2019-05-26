package ru.chufeng.plsqllang.server.database;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import ru.chufeng.plsqllang.server.PlSqlLangServer;

import java.sql.*;
import java.util.ArrayList;

public class DdlGen {
    private String connectionString;
    private String objectName;
    private String objectType;
    private PlSqlLangServer languageServer;

    //DBMS_METADATA.GET_DDL (
    //object_type     IN VARCHAR2,
    //name            IN VARCHAR2,
    //schema          IN VARCHAR2 DEFAULT NULL,
    //version         IN VARCHAR2 DEFAULT 'COMPATIBLE',
    //model           IN VARCHAR2 DEFAULT 'ORACLE',
    //transform       IN VARCHAR2 DEFAULT 'DDL')
    //RETURN CLOB;


    public DdlGen(String connectionString, String objectName, String objectType, PlSqlLangServer languageServer) {
        this.connectionString = connectionString;
        this.objectName = objectName;
        this.objectType = objectType;
        this.languageServer = languageServer;
    }

    public String get() {
        String result = "";
        try (Connection conn = ConnectionSingleton.getInstance(connectionString).get()){
            CallableStatement stmt = conn.prepareCall("{? = call DBMS_METADATA.GET_DDL(?, ?)}");

            stmt.registerOutParameter (1, Types.CLOB);

            stmt.setString (2, objectType);
            stmt.setString (3, objectName);

            stmt.execute ();

            Clob ddl = stmt.getClob (1);
            result = ddl.getSubString(1, (int) ddl.length());
        } catch(SQLException se) {
            languageServer.getLanguageClient().telemetryEvent(se.getMessage());
        }
        JsonObject json = new JsonObject();
        json.add("name", new JsonPrimitive(objectName));
        json.add("type", new JsonPrimitive(objectType));
        json.add("ddl", new JsonPrimitive(result));
        return json.getAsString();
    }

}
