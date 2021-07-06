package ru.chufeng.plsqllang.server.database;

import com.google.gson.Gson;
import ru.chufeng.plsqllang.server.PlSqlLangServer;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DdlGen {
    private String connectionString;
    private String objectName;
    private String objectType;
    private PlSqlLangServer languageServer;

    public DdlGen(String connectionString, String objectName, String objectType, PlSqlLangServer languageServer) {
        this.connectionString = connectionString;
        this.objectName = objectName;
        this.objectType = objectType;
        this.languageServer = languageServer;
    }

    public String get() {
        String result = "";
        try (Connection conn = ConnectionPool.getInstance().get(connectionString)){
//        try (Connection conn = ConnectionPool.getInstance(connectionString).get()){
            CallableStatement stmt = conn.prepareCall("{? = call DBMS_METADATA.GET_DDL(?, ?)}");

            stmt.registerOutParameter (1, Types.CLOB);

            stmt.setString (2, objectType);
            stmt.setString (3, objectName);

            stmt.execute ();

            Clob ddl = stmt.getClob (1);
            result = ddl.getSubString(1, (int) ddl.length());
        } catch(SQLException se) {
//            System.out.println(se.getMessage());
            languageServer.getLanguageClient().telemetryEvent(se.getMessage());
        }
        Map<String,String> map = new HashMap<>();
        map.put("name", objectName);
        map.put("type", objectType);
        map.put("ddl", result);
        return new Gson().toJson(map);
    }

//    public static void main(String[] args) {
//        DdlGen gen = new DdlGen("biss_dev2/biss@milesplus2:1521/biss", "V_FORM_UESTOER", "VIEW", null);
//        System.out.println(gen.get());
//    }

}
