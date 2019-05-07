package ru.chufeng.plsqllang.server;

import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.InitializeResult;
import org.eclipse.lsp4j.ServerCapabilities;
import org.eclipse.lsp4j.TextDocumentSyncKind;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.lsp4j.services.WorkspaceService;

import java.util.concurrent.CompletableFuture;


public class PlSqlLangServer implements LanguageServer {

    private final PlSqlTextDocumentService plSqlTextDocumentService;
    private final PlSqlWorkspaceService plSqlWorkspaceService;
    private LanguageClient languageClient;

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
        InitializeResult result = new InitializeResult(capabilities);
        return CompletableFuture.completedFuture(result);
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
