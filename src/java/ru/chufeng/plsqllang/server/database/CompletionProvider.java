package ru.chufeng.plsqllang.server.database;

import com.google.gson.Gson;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import ru.chufeng.plsqllang.server.PlSqlLangServer;

import java.sql.*;
import java.util.ArrayList;

public class CompletionProvider {
    private String connectionString;
    private PlSqlLangServer languageServer;

    public CompletionProvider(PlSqlLangServer languageServer) {
        this.languageServer = languageServer;
        this.connectionString = languageServer.getActiveConnection();
    }

    public ArrayList<CompletionItem> getTables() {
        ArrayList<CompletionItem> result = new ArrayList<>();
        String sql = "select * from user_objects where object_type IN ('TABLE', 'VIEW')";

        try (Connection conn = ConnectionPool.getInstance().get(connectionString)){

            Statement stmt;
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                CompletionItem item = new CompletionItem(rs.getString(1));
                item.setKind(CompletionItemKind.EnumMember);
                result.add(item);
            }

        } catch(SQLException se) {
            languageServer.getLanguageClient().telemetryEvent(se.getMessage());
        }
        return result;

    }
}
