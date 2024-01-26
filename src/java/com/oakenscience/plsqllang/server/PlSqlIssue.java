package com.oakenscience.plsqllang.server;

import org.eclipse.lsp4j.DiagnosticSeverity;

public class PlSqlIssue {
    private int line;
    private int column;
    private String description;
    private DiagnosticSeverity severity;

    public PlSqlIssue(int line, int column, String description) {
        this.line = line;
        this.column = column;
        this.description = description;
    }

    public PlSqlIssue(int line, int column, String description, DiagnosticSeverity severity) {
        this.line = line;
        this.column = column;
        this.description = description;
        this.severity = severity;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DiagnosticSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(DiagnosticSeverity severity) {
        this.severity = severity;
    }
}
