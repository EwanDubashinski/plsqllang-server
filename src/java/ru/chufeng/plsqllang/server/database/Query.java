package ru.chufeng.plsqllang.server.database;

import com.google.gson.Gson;
import ru.chufeng.plsqllang.server.PlSqlLangServer;

import java.sql.*;
import java.util.ArrayList;

public class Query {
    private String connectionString;
    private String sql;
    private PlSqlLangServer languageServer;

    public Query(String connectionString, String sql, PlSqlLangServer languageServer) {
        this.connectionString = connectionString;
        this.sql = sql;
        this.languageServer = languageServer;
    }

    public String getResultsJSON() {
        ArrayList<ArrayList<String>> result = new ArrayList<>();

        try (Connection conn = ConnectionPool.getInstance().get(connectionString)){

            Statement stmt;
            stmt = conn.createStatement();
            stmt.setMaxRows(50); // TODO implement pagination
            ResultSet rs = stmt.executeQuery(sql);
            rs.next();
            int count = rs.getMetaData().getColumnCount();
            ArrayList<String> headers = new ArrayList<>();
            for (int i = 1; i <=count ; i++) {
                headers.add(rs.getMetaData().getColumnName(i));
            }
            result.add(headers);
            do {
                ArrayList<String> row = new ArrayList<>();
                for (int i = 1; i <= count; i++) {
                    int type = rs.getMetaData().getColumnType(i);
                    String cell;

                    try {
                        if (type == Types.CLOB) {
                            Clob ddl = rs.getClob (i);
                            cell = ddl.getSubString(1, 20);
                        } else if (type == Types.BLOB) {
                            cell = "*** BLOB ***";
                        } else {
                            cell = rs.getString(i);
                        }
                    } catch (Exception e) {
                        cell = "unsupported data type";
                    }
                    row.add(cell);
                }
                result.add(row);
            } while(rs.next());

        } catch(SQLException se) {
            languageServer.getLanguageClient().telemetryEvent(se.getMessage());
        }
        return new Gson().toJson(result);

    }
}
