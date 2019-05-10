package ru.chufeng.plsqllang.server.database;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class ObjectCollection {
    static final String JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";
//    static String dbUrl = "jdbc:oracle:thin:@10.233.106.185:1521:BISS";

    String user;
    String pass;
    String dbUrl;

    private String connectionString;
    private String objectType;
    private ArrayList<String> objects = new ArrayList<>();
    private String sql = "SELECT object_name FROM user_objects WHERE object_type = replace('%s', '_', ' ') ORDER BY 1";

    public ObjectCollection(String connectionString, String objectType) {
        this.connectionString = connectionString;

        user = connectionString.substring(0, connectionString.indexOf("/"));
        pass = connectionString.substring(connectionString.indexOf("/") + 1, connectionString.indexOf("@"));
        dbUrl = "jdbc:oracle:thin:" + connectionString.substring(connectionString.indexOf("@")).replace("/", ":");

        this.objectType = objectType;
        this.sql = String.format(sql, objectType);
        fillObjects();
    }

    private void fillObjects() {

        try (Connection conn = DriverManager.getConnection(dbUrl, user, pass)){

            Statement stmt;
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()) {
                objects.add(rs.getString(1));
            }

        } catch(SQLException se) {
            se.printStackTrace();
        }
    }

    public String getSerializedArray() {
        return new Gson().toJson(objects);
    }

//    public static void main(String[] args) {
////        ObjectCollection collection = new ObjectCollection("biss...", ObjectType.PACKAGE_BODY);
////        System.out.println(collection.getSerializedArray());
//        Type itemsMapType = new TypeToken<Map<String, String>>() {}.getType();
//        Gson gson = new Gson();
//        Map<String, String> map = gson.fromJson("{connection: 'biss_dev2/biss@milesplus2:1521/biss', object_type: this.object_type}", itemsMapType);
//        System.out.println(map.get("object_type"));
//    }
}
