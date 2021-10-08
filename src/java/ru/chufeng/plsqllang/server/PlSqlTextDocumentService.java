package ru.chufeng.plsqllang.server;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ConsoleErrorListener;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.TextDocumentService;
import ru.chufeng.plsqllang.parser.PlSqlLexer;
import ru.chufeng.plsqllang.parser.PlSqlParser;
import ru.chufeng.plsqllang.server.database.CompletionProvider;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class PlSqlTextDocumentService implements TextDocumentService {

    private final PlSqlLangServer plSqlLangServer;
    private final HashMap<String, String> openDocuments = new HashMap<>();
    private ParseTree tree;

    public PlSqlTextDocumentService(PlSqlLangServer plSqlLangServer) {
        this.plSqlLangServer = plSqlLangServer;
    }

    @Override
    public void didOpen(DidOpenTextDocumentParams didOpenTextDocumentParams) {
        TextDocumentItem document = didOpenTextDocumentParams.getTextDocument();
        String uri = document.getUri();
        List<Diagnostic> diagnostics = validateDocument(uri, document.getText());
        openDocuments.put(uri, document.getText());
        plSqlLangServer.getLanguageClient().publishDiagnostics(new PublishDiagnosticsParams(uri, diagnostics));
    }

    @Override
    public void didChange(DidChangeTextDocumentParams didChangeTextDocumentParams) {
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
    }

//    @Override
//    public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(CompletionParams position) {
//        final List<CompletionItem> completions = new ArrayList<>();
//        return CompletableFuture.supplyAsync(() -> {
//            int lineNum = position.getPosition().getLine();
//            int character = position.getPosition().getCharacter();
//            String text = openDocuments.get(position.getTextDocument().getUri());
//            BufferedReader reader = new BufferedReader(new StringReader(text));
//            String line = reader.lines().skip(lineNum).findFirst().orElse(null);
//            if (plSqlLangServer.isConnected() && line != null && line.toUpperCase().substring(0, character).trim().endsWith("FROM")) {
//                CompletionProvider provider = new CompletionProvider(plSqlLangServer);
//                completions.addAll(provider.getTables());
//            }
//
//            return Either.forLeft(completions);
//        });
//    }

    public List<Diagnostic> validateDocument(String documentUri, String documentContent) {
        List<Diagnostic> diagnostics = new ArrayList<>();

        PlSqlLexer lexer = new PlSqlLexer(CharStreams.fromString(documentContent.toUpperCase()));
        PlSqlParser parser = new PlSqlParser(new CommonTokenStream(lexer));
        SyntaxErrorListener errorListener = new SyntaxErrorListener();
        parser.removeErrorListener(ConsoleErrorListener.INSTANCE);
        parser.addErrorListener(errorListener);
        tree = parser.sql_script();

        PlSqlIssue issue;

        for (SyntaxError error : errorListener.getSyntaxErrors()) {

            issue = new PlSqlIssue(error.getLine(), error.getCharPositionInLine(), error.getMessage(), DiagnosticSeverity.Error);

            Position start = new Position(issue.getLine() - 1, issue.getColumn());
            Position end = new Position(issue.getLine() - 1, issue.getColumn() + error.getOffendingSymbol().getText().length());

            Diagnostic diagnostic = new Diagnostic(new Range(start, end), issue.getDescription(), issue.getSeverity(), Constants.LANGUAGE);
            diagnostics.add(diagnostic);
        }

        return diagnostics;
    }

//    @Override
//    public CompletableFuture<List<Either<SymbolInformation, DocumentSymbol>>> documentSymbol(DocumentSymbolParams params) {
////        String text = openDocuments.get(params.getTextDocument().getUri());
////        tree.getChildCount();
//        ParseTreeWalker walker = new ParseTreeWalker();
//        SymbolsListener symbolsListener = new SymbolsListener();
//
//        walker.walk(symbolsListener, tree);
//
//        return CompletableFuture.completedFuture(symbolsListener.getSymbols());
//    }
}
