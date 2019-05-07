package ru.chufeng.plsqllang.server;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.TextDocumentService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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
    }

    private List<Diagnostic> validateDocument(String xmlDocumentUri, String xmlDocumentContent) {
        List<Diagnostic> diagnostics = new ArrayList<>();

        PlSqlIssue issue = new PlSqlIssue(2, 3, "Test issue");
        issue.setSeverity(DiagnosticSeverity.Warning);

        Position start = new Position(issue.getLine() - 1, issue.getColumn() - 1);
        Position end = new Position(issue.getLine() - 1, issue.getColumn() - 1);

        end.setCharacter(9);

        Diagnostic diagnostic = new Diagnostic(new Range(start, end), issue.getDescription(), issue.getSeverity(), Constants.LANGUAGE);
        diagnostics.add(diagnostic);

        return diagnostics;
    }
}
