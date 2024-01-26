package com.oakenscience.plsqllang.server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oakenscience.plsqllang.server.database.ConnectionPool;
import com.oakenscience.plsqllang.server.database.DdlGen;
import com.oakenscience.plsqllang.server.database.ObjectCollection;
import com.oakenscience.plsqllang.server.database.Query;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.services.JsonNotification;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.lsp4j.services.WorkspaceService;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


public class PlSqlLangServer implements LanguageServer {

    private final PlSqlTextDocumentService plSqlTextDocumentService;
    private final PlSqlWorkspaceService plSqlWorkspaceService;
    private LanguageClient languageClient;
    private String activeConnection;

    public PlSqlLangServer() {
        plSqlTextDocumentService = new PlSqlTextDocumentService(this);
        plSqlWorkspaceService = new PlSqlWorkspaceService(this);
    }

    public void setClient(LanguageClient client) {
        this.languageClient = client;
    }

    public LanguageClient getLanguageClient() {
        return languageClient;
    }

    public CompletableFuture<InitializeResult> initialize(InitializeParams initializeParams) {
        ServerCapabilities capabilities = new ServerCapabilities();
        capabilities.setTextDocumentSync(TextDocumentSyncKind.Full);

//        final CompletionOptions completionOptions = new CompletionOptions();
//        completionOptions.setTriggerCharacters(Arrays.asList("FROM ", "from "));

//        capabilities.setCompletionProvider(completionOptions);
//        capabilities.setDocumentSymbolProvider(true);

        InitializeResult result = new InitializeResult(capabilities);
        return CompletableFuture.completedFuture(result);
    }

    @JsonRequest("getTree")
    public CompletableFuture<String> getTree(Object params) {
        Type itemsMapType = new TypeToken<Map<String, String>>() {}.getType();
        Gson gson = new Gson();
        Map<String, String> map = gson.fromJson(params.toString(), itemsMapType);
        return CompletableFuture.completedFuture(new ObjectCollection(map.get("connection"), map.get("object_type")).getSerializedArray());
//        return CompletableFuture.completedFuture("ready! =)");
    }

    @JsonRequest("getQueryResults")
    public CompletableFuture<String> getQueryResults(String params) {
        Type itemsMapType = new TypeToken<Map<String, String>>() {}.getType();
        Gson gson = new Gson();
        Map<String, String> map = gson.fromJson(params, itemsMapType);
//        languageClient.telemetryEvent("Test telemetryEvent");
        return CompletableFuture.completedFuture(new Query(map.get("connection"), map.get("query"), this).getResultsJSON());
    }

    @JsonRequest("getDDL")
    public CompletableFuture<String> getDDL(String params) {
        Type itemsMapType = new TypeToken<Map<String, String>>() {}.getType();
        Gson gson = new Gson();
        Map<String, String> map = gson.fromJson(params, itemsMapType);
        languageClient.telemetryEvent("getDDL: " + map.get("name"));
        return CompletableFuture.completedFuture(new DdlGen(map.get("connection"), map.get("name"), map.get("type"), this).get());
    }

    @JsonNotification("activateConnection")
    public void activateConnection(String connectionString) {
        this.activeConnection = connectionString;
        ConnectionPool.getInstance().get(connectionString);
    }

    public String getActiveConnection() {
        return activeConnection;
    }

    public boolean isConnected() {
        return this.activeConnection != null;
    }

    public CompletableFuture<Object> shutdown() {
        return null;
    }

    public void exit() {
        System.exit(0);
    }

    public TextDocumentService getTextDocumentService() {
        return plSqlTextDocumentService;
    }

    public WorkspaceService getWorkspaceService() {
        return plSqlWorkspaceService;
    }

}
