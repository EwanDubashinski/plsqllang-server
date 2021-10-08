package ru.chufeng.plsqllang.server;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ConsoleErrorListener;
import org.antlr.v4.runtime.tree.ParseTree;
import ru.chufeng.plsqllang.parser.*;

import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class PlSqlServerStdioLauncher {
    public static void main(String[] args) {
        PlSqlLangServer server = new PlSqlLangServer();
//        PrintWriter writer = new PrintWriter("C:\\Source\\plsqllang\\client\\plsql-lsp\\server\\trace_plsql_server.log");
        Launcher<LanguageClient> launcher = LSPLauncher.createServerLauncher(server, System.in, System.out, false, null);
        server.setClient(launcher.getRemoteProxy());
        launcher.startListening();
    }
}
