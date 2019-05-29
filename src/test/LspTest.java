import org.junit.jupiter.api.*;
import static org.junit.Assert.assertEquals;

public class LspTest {
    @Test
    @DisplayName("1 + 1 = 2")
    void addsTwoNumbers() {
        assertEquals("1 + 1 should equal 2", 2, (1+1));
    }
}
