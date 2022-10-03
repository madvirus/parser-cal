package cal.ast;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DebugStringTest {
    @Test
    void infix() {
        Exp left = new NumberExp("1");
        Exp right = new IdExp("a");
        InfixOpExp inExp = new InfixOpExp("+", left, right);
        assertThat(inExp.debugString()).isEqualTo("(1 + a)");
    }
}
