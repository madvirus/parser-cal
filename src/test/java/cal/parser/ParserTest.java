package cal.parser;

import cal.ast.*;
import cal.lexer.Lexer;
import cal.token.TokenType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ParserTest {
    private static void assertExpStmtWithNumber(Stmt stmt, double expected) {
        assertThat(stmt).isInstanceOf(ExpStmt.class);
        ExpStmt expStmt = (ExpStmt) stmt;
        Exp exp = expStmt.getExp();
        assertNumExp(exp, expected);
    }

    private static void assertNumExp(Exp exp, double expected) {
        assertThat(exp).isInstanceOf(NumberExp.class);
        NumberExp numberExp = (NumberExp) exp;
        assertThat(numberExp.valueAsDouble()).isEqualTo(expected);
    }

    private void assertExpStmtWithId(Stmt stmt, String expected) {
        assertThat(stmt).isInstanceOf(ExpStmt.class);
        ExpStmt expStmt = (ExpStmt) stmt;
        Exp exp = expStmt.getExp();
        assertIdExp(exp, expected);
    }

    private static void assertIdExp(Exp exp, String expected) {
        assertThat(exp).isInstanceOf(IdExp.class);
        IdExp idExp = (IdExp) exp;
        assertThat(idExp.getId()).isEqualTo(expected);
    }

    @Test
    void numberExp() {
        String input = "1\n2 \n 3";
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        Stmt[] stmts = program.getStmts();
        assertExpStmtWithNumber(stmts[0], 1.0);
        assertExpStmtWithNumber(stmts[1], 2.0);
        assertExpStmtWithNumber(stmts[2], 3.0);
    }

    @Test
    void idExp() {
        String input = "abc \n a123";
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        Stmt[] stmts = program.getStmts();
        assertExpStmtWithId(stmts[0], "abc");
        assertExpStmtWithId(stmts[1], "a123");
    }

    @Test
    void letStmt() {
        String input = "let abc = 10";
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        Stmt[] stmts = program.getStmts();
        Stmt stmt = stmts[0];
        assertThat(stmt).isInstanceOf(LetStmt.class);
        LetStmt letStmt = (LetStmt) stmt;
        assertIdExp(letStmt.getIdExp(), "abc");
        assertNumExp(letStmt.getExp(), 10.0);
    }

    @Test
    void infixOp() {
        String input = "1 + 10";
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        Stmt[] stmts = program.getStmts();
        ExpStmt expStmt = (ExpStmt) stmts[0];
        Exp exp = expStmt.getExp();
        assertThat(exp).isInstanceOf(InfixOpExp.class);
        InfixOpExp inExp = (InfixOpExp) exp;
        assertThat(inExp.getOperator()).isEqualTo("+");
        assertNumExp(inExp.getLeft(), 1.0);
        assertNumExp(inExp.getRight(), 10.0);
    }

    @Test
    void prefixOp() {
        String input = "-1";
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        Stmt[] stmts = program.getStmts();
        ExpStmt expStmt = (ExpStmt) stmts[0];
        Exp exp = expStmt.getExp();
        assertThat(exp).isInstanceOf(PrefixOpExp.class);
        PrefixOpExp inExp = (PrefixOpExp) exp;
        assertThat(inExp.getOperator()).isEqualTo("-");
        assertNumExp(inExp.getExp(), 1.0);
    }

    @Test
    void program() {
        assertProgram("1 + 10", "(1 + 10)");
        assertProgram("1 + 10 * 5", "(1 + (10 * 5))");
        assertProgram("1 + 2 + 3", "((1 + 2) + 3)");
        assertProgram("(1 + 2) * 3", "((1 + 2) * 3)");
        assertProgram("(1 + 2) * 3 ** 2", "((1 + 2) * (3 ** 2))");
        assertProgram("-1 * 3", "((-1) * 3)");
        assertProgram("-2 ** 3", "(-(2 ** 3))");
        assertProgram("--1", "(-(-1))");
        assertProgram("let a = 10\na * (3 + 5)",
                "(a = 10)", "(a * (3 + 5))");
    }

    private void assertProgram(String input, String ...expectedStmt) {
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        Stmt[] stmts = program.getStmts();
        for (int i = 0 ; i < expectedStmt.length ; i++) {
            assertThat(stmts[i].debugString()).isEqualTo(expectedStmt[i]);
        }
    }

    @Test
    void errors() {
        String[] errors1 = getParseError("123 456");
        assertErrorContains(errors1, TokenType.NUMBER, "456");

        String[] errors2 = getParseError("(123 + 456 1");
        assertErrorContains(errors2, TokenType.RPAREN, "1");

        String[] errors3 = getParseError("(123 + 456)) + 1");
        assertErrorContains(errors3, TokenType.RPAREN, ")");

        String[] errors4 = getParseError("-");
        assertErrorContains(errors4, TokenType.MINUS, "-");
    }

    private static void assertErrorContains(String[] errors, TokenType type, String literal) {
        assertThat(errors).anyMatch(s -> s.contains(type.name()));
        assertThat(errors).anyMatch(s -> s.contains(literal));
    }

    private static String[] getParseError(String input) {
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        parser.parseProgram();
        String[] errors = parser.getErrors();
        return errors;
    }
}
