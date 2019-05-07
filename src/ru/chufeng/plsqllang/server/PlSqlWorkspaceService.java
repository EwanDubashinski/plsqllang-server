package ru.chufeng.plsqllang.server;

import com.google.gson.Gson;
import org.eclipse.lsp4j.DidChangeConfigurationParams;
import org.eclipse.lsp4j.DidChangeWatchedFilesParams;
import org.eclipse.lsp4j.services.WorkspaceService;

public class PlSqlWorkspaceService implements WorkspaceService {
    private final PlSqlLangServer plSqlLangServer;

    public PlSqlWorkspaceService(PlSqlLangServer plSqlLangServer) {
        this.plSqlLangServer = plSqlLangServer;
    }

    @Override
    public void didChangeConfiguration(DidChangeConfigurationParams didChangeConfigurationParams) {
    }

    @Override
    public void didChangeWatchedFiles(DidChangeWatchedFilesParams didChangeWatchedFilesParams) {

    }
}
