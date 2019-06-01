package ru.chufeng.plsqllang.server;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ConsoleErrorListener;
import org.antlr.v4.runtime.tree.ParseTree;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.TextDocumentService;
import ru.chufeng.plsqllang.parser.PlSqlLexer;
import ru.chufeng.plsqllang.parser.PlSqlParser;
import ru.chufeng.plsqllang.server.database.CompletionProvider;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.Lock;

public class PlSqlTextDocumentService implements TextDocumentService {

    private final PlSqlLangServer plSqlLangServer;
    private final HashMap<String, String> openDocuments = new HashMap<>();

    public PlSqlTextDocumentService(PlSqlLangServer plSqlLangServer) {
        this.plSqlLangServer = plSqlLangServer;
    }

    @Override
    public void didOpen(DidOpenTextDocumentParams didOpenTextDocumentParams) {
//        System.out.println("didOpen");
        TextDocumentItem document = didOpenTextDocumentParams.getTextDocument();
        String uri = document.getUri();
        List<Diagnostic> diagnostics = validateDocument(uri, document.getText());
        openDocuments.put(uri, document.getText());
        plSqlLangServer.getLanguageClient().publishDiagnostics(new PublishDiagnosticsParams(uri, diagnostics));
    }

    @Override
    public void didChange(DidChangeTextDocumentParams didChangeTextDocumentParams) {
//        System.out.println("didChange");
        VersionedTextDocumentIdentifier versionedTextDocumentIdentifier = didChangeTextDocumentParams.getTextDocument();
        String uri = versionedTextDocumentIdentifier.getUri();
        Iterator<TextDocumentContentChangeEvent> textDocumentContentChangeEventIterator = didChangeTextDocumentParams.getContentChanges().iterator();
        List<Diagnostic> diagnostics = new ArrayList<>();

        while (textDocumentContentChangeEventIterator.hasNext()) {
            TextDocumentContentChangeEvent textDocumentContentChangeEvent = textDocumentContentChangeEventIterator.next();
            String text = textDocumentContentChangeEvent.getText();
            openDocuments.put(uri, text);
            List<Diagnostic> currentDiagnostics = validateDocument(uri, text);
            diagnostics.addAll(currentDiagnostics);
        }
        plSqlLangServer.getLanguageClient().publishDiagnostics(new PublishDiagnosticsParams(uri, diagnostics));
    }

    @Override
    public void didClose(DidCloseTextDocumentParams didCloseTextDocumentParams) {
        String uri = didCloseTextDocumentParams.getTextDocument().getUri();
        plSqlLangServer.getLanguageClient().publishDiagnostics(new PublishDiagnosticsParams(uri, new ArrayList<>()));
        openDocuments.remove(uri);
    }

    @Override
    public void didSave(DidSaveTextDocumentParams didSaveTextDocumentParams) {
        /* TODO depends on settings: check on change or on save */
//        String uri = didSaveTextDocumentParams.getTextDocument().getUri();
//        List<Diagnostic> diagnostics = validateDocument(uri, didSaveTextDocumentParams.getText());
//        plSqlLangServer.getLanguageClient().publishDiagnostics(new PublishDiagnosticsParams(uri, diagnostics));
    }

    @Override
    public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(CompletionParams position) {
        final List<CompletionItem> completions = new ArrayList<>();
        return CompletableFuture.supplyAsync(() -> {
            int lineNum = position.getPosition().getLine();
            int character = position.getPosition().getCharacter();
            String text = openDocuments.get(position.getTextDocument().getUri());
//            String line = String.valueOf(text.length());
            BufferedReader reader = new BufferedReader(new StringReader(text));
            String line = reader.lines().skip(lineNum).findFirst().orElse(null);
            if (plSqlLangServer.isConnected() && line != null && line.toUpperCase().substring(0, character).trim().endsWith("FROM")) {
//                CompletionItem item = new CompletionItem(line);
//                item.setKind(CompletionItemKind.EnumMember);
//                completions.add(item);
                CompletionProvider provider = new CompletionProvider(plSqlLangServer);
                completions.addAll(provider.getTables());
            }


//            position.getContext();
//            String fileUri = position.getTextDocument().getUri();

            return Either.forLeft(completions);
        });
    }

    private List<Diagnostic> validateDocument(String documentUri, String documentContent) {
//        System.out.println("validateDocument start: " + System.currentTimeMillis());
        List<Diagnostic> diagnostics = new ArrayList<>();

        PlSqlLexer lexer = new PlSqlLexer(CharStreams.fromString(documentContent.toUpperCase()));
        PlSqlParser parser = new PlSqlParser(new CommonTokenStream(lexer));
        SyntaxErrorListener errorListener = new SyntaxErrorListener();
        parser.removeErrorListener(ConsoleErrorListener.INSTANCE);
        parser.addErrorListener(errorListener);
        ParseTree tree = parser.sql_script();

//        System.out.println("validateDocument start: " + System.currentTimeMillis());
        PlSqlIssue issue;

        for (SyntaxError error : errorListener.getSyntaxErrors()) {
//            System.out.println(error);

            issue = new PlSqlIssue(error.getLine(), error.getCharPositionInLine(), error.getMessage(), DiagnosticSeverity.Error);

            Position start = new Position(issue.getLine() - 1, issue.getColumn());
            Position end = new Position(issue.getLine() - 1, issue.getColumn() + error.getOffendingSymbol().getText().length());

            Diagnostic diagnostic = new Diagnostic(new Range(start, end), issue.getDescription(), issue.getSeverity(), Constants.LANGUAGE);
            diagnostics.add(diagnostic);
        }
//
//        issue = new PlSqlIssue(1, 1, "Test issue ", DiagnosticSeverity.Error);
//        Position start = new Position(issue.getLine() - 1, issue.getColumn() - 1);
//        Position end = new Position(issue.getLine() - 1, issue.getColumn() + 1);
//        Diagnostic diagnostic = new Diagnostic(new Range(start, end), issue.getDescription(), issue.getSeverity(), Constants.LANGUAGE);
//        diagnostics.add(diagnostic);



        return diagnostics;
    }
}
