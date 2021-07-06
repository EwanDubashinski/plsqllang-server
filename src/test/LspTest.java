import org.eclipse.lsp4j.Diagnostic;
import org.junit.jupiter.api.*;
import ru.chufeng.plsqllang.server.PlSqlTextDocumentService;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class LspTest {
    @Test
    @DisplayName("SELECT NULL FROM DUAL")
    void select_null() {
        String code = "SELECT NULL FROM DUAL";
        PlSqlTextDocumentService documentService = new PlSqlTextDocumentService(null);
        List<Diagnostic> diagnostics = documentService.validateDocument("", code);
        assertEquals("syntax errors count should be 0", 0, diagnostics.size());
    }
}
