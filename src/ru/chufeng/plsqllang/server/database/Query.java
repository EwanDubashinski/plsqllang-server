package ru.chufeng.plsqllang.server.database;

import com.google.gson.Gson;
import org.eclipse.lsp4j.services.LanguageServer;
import ru.chufeng.plsqllang.server.PlSqlLangServer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

        try (Connection conn = ConnectionSingleton.getInstance(connectionString).get()){

            Statement stmt;
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()) {
                int count = rs.getMetaData().getColumnCount();
                ArrayList<String> row = new ArrayList<>();
                for (int i = 1; i <= count; i++) {
                    row.add(rs.getString(i));
                }
                result.add(row);
            }

        } catch(SQLException se) {
            languageServer.getLanguageClient().telemetryEvent(se.getMessage());
        }
        return new Gson().toJson(result);

    }
}
