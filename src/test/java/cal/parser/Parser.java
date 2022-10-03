package cal.parser;

import cal.ast.*;
import cal.lexer.Lexer;
import cal.token.Token;
import cal.token.TokenType;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private Lexer lexer;
    private Token curToken;
    private Token peekToken;
    private List<String> errors = new ArrayList<>();

    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    private void nextToken() {
        curToken = peekToken;
        peekToken = lexer.nextToken();
    }

    public Program parseProgram() {
        nextToken();
        nextToken();

        List<Stmt> stmts = new ArrayList<>();
        while (curToken.getType() != TokenType.EOF) {
            Stmt stmt = parseStmt();
            if (stmt != null) {
                stmts.add(stmt);
            }
            nextToken();
            if (curToken.getType() == TokenType.NEWLINE) {
                nextToken();
            } else if (curToken.getType() != TokenType.EOF) {
                addErrors("Unexpected Token after stmt: %s".formatted(curToken));
            }
        }
        return new Program(stmts.toArray(new Stmt[stmts.size()]));
    }

    private Stmt parseStmt() {
        Stmt stmt = null;
        if (curToken.getType() == TokenType.LET) {
            stmt = parseLetStmt();
        } else {
            stmt = parseExpStmt();
        }
        return stmt;
    }

    private Stmt parseLetStmt() {
        if (!expectPeek(TokenType.ID)) {
            return null;
        }
        IdExp idExp = new IdExp(curToken.getLiteral());
        if (!expectPeek(TokenType.ASSIGN)) {
            return null;
        }
        nextToken();
        Exp exp = parseExp(0);
        if (exp == null) {
            return null;
        }
        return new LetStmt(idExp, exp);
    }

    private boolean expectPeek(TokenType type) {
        if (peekToken.getType() != type) {
            addErrors("Expected Token Type %s, but %s".formatted(type, curToken));
            return false;
        }
        nextToken();
        return true;
    }

    private Stmt parseExpStmt() {
        Exp exp = parseExp(0);
        if (exp == null) {
            return  null;
        }
        return new ExpStmt(exp);
    }

    private Exp parseExp(int prec) {
        Exp exp = null;
        if (curToken.getType() == TokenType.NUMBER) {
            exp = new NumberExp(curToken.getLiteral());
        } else if (curToken.getType() == TokenType.ID) {
            exp = new IdExp(curToken.getLiteral());
        } else if (curToken.getType() == TokenType.LPAREN) {
            exp = parseParan();
        } else if (curToken.getType() == TokenType.MINUS) {
            exp = parsePrefixOp();
        }
        if (exp == null) {
            return null;
        }
        while (infixOp(peekToken.getType()) &&
                prec < precedence(peekToken.getType())) {
            nextToken();
            String operator = curToken.getLiteral();
            int curPrec = precedence(curToken.getType());
            nextToken();
            Exp right = parseExp(curPrec);
            exp = new InfixOpExp(operator, exp, right);
        }
        return exp;
    }

    private Exp parsePrefixOp() {
        Token prefixOpToken = curToken;
        String operator = curToken.getLiteral();
        nextToken();
        Exp exp = parseExp(5);
        if (exp == null) {
            errors.add("Prefix Operator has no operand exp: prefix operator: %s".formatted(prefixOpToken));
            return null;
        }
        return new PrefixOpExp(operator, exp);
    }

    private Exp parseParan() {
        nextToken();
        Exp exp = parseExp(0);
        if (exp == null) {
            return null;
        }
        if (!expectPeek(TokenType.RPAREN)) {
            return null;
        }
        return exp;
    }

    private int precedence(TokenType type) {
        return switch(type) {
            case PLUS, MINUS -> 1;
            case MULTIPLY, DIVIE -> 3;
            case POW -> 7;
            default -> 0;
        };
    }

    private boolean infixOp(TokenType type) {
        return type == TokenType.PLUS ||
                type == TokenType.MINUS ||
                type == TokenType.MULTIPLY ||
                type == TokenType.DIVIE ||
                type == TokenType.POW;
    }

    private void addErrors(String error) {
        errors.add(error);
    }

    public String[] getErrors() {
        return errors.toArray(new String[errors.size()]);
    }
}
