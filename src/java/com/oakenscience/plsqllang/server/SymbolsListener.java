package com.oakenscience.plsqllang.server;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import com.oakenscience.plsqllang.parser.PlSqlParser;
import com.oakenscience.plsqllang.parser.PlSqlParserBaseListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SymbolsListener extends PlSqlParserBaseListener {

    private List<Either<SymbolInformation, DocumentSymbol>> symbols = new ArrayList<>();

    public List<Either<SymbolInformation, DocumentSymbol>> getSymbols() {
        return symbols;
    }

    @Override
    public void enterProcedure_body(PlSqlParser.Procedure_bodyContext ctx) {
        String id = ctx.identifier().getText().toLowerCase();
        Position idStart = new Position(ctx.identifier().start.getLine() - 1, ctx.identifier().start.getCharPositionInLine());
        Position idStop = new Position(ctx.identifier().stop.getLine() - 1, ctx.identifier().stop.getCharPositionInLine());

        Position start = new Position(ctx.start.getLine() - 1, ctx.start.getCharPositionInLine());
        Position stop = new Position(ctx.stop.getLine() - 1, ctx.stop.getCharPositionInLine());

        Range range = new Range(start, stop);
        Range selectionRange = new Range(idStart, idStop);

        DocumentSymbol symbol = new DocumentSymbol(id, SymbolKind.Function, range, selectionRange);
        symbols.add(Either.<SymbolInformation, DocumentSymbol>forRight(symbol));

//         ctx.start.getLine();
//        super.enterProcedure_body(ctx);
    }

    @Override
    public void enterFunction_body(PlSqlParser.Function_bodyContext ctx) {
        String id = ctx.identifier().getText().toLowerCase();
        Position idStart = new Position(ctx.identifier().start.getLine() - 1, ctx.identifier().start.getCharPositionInLine());
        Position idStop = new Position(ctx.identifier().stop.getLine() - 1, ctx.identifier().stop.getCharPositionInLine());

        Position start = new Position(ctx.start.getLine() - 1, ctx.start.getCharPositionInLine());
        Position stop = new Position(ctx.stop.getLine() - 1, ctx.stop.getCharPositionInLine());

        Range range = new Range(start, stop);
        Range selectionRange = new Range(idStart, idStop);

        DocumentSymbol symbol = new DocumentSymbol(id, SymbolKind.Function, range, selectionRange);
        symbols.add(Either.<SymbolInformation, DocumentSymbol>forRight(symbol));
    }
}
