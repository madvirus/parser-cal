package cal.lexer;

import cal.token.Token;
import cal.token.TokenType;

public class Lexer {
    private String input;

    private char curCh;
    private int curIdx;
    private int peekIdx;

    public Lexer(String input) {
        this.input = input;
        readChar();
    }

    private void readChar() {
        if (peekIdx == input.length()) {
            curCh = 0;
            curIdx = peekIdx;
        } else {
            curIdx = peekIdx;
            curCh = input.charAt(curIdx);
            peekIdx++;
        }
    }

    public Token nextToken() {
        skipSpaces();
        Token t = null;
        if (curCh == 0) {
            t = new Token(TokenType.EOF, null);
        } else if (curCh == '+') {
            t = new Token(TokenType.PLUS, String.valueOf(curCh));
        } else if (curCh == '-') {
            t = new Token(TokenType.MINUS, String.valueOf(curCh));
        } else if (curCh == '*') {
            if (peekCh() == '*') {
                readChar();
                t = new Token(TokenType.POW, "**");
            } else {
                t = new Token(TokenType.MULTIPLY, String.valueOf(curCh));
            }
        } else if (curCh == '/') {
            t = new Token(TokenType.DIVIE, String.valueOf(curCh));
        } else if (curCh == '(') {
            t = new Token(TokenType.LPAREN, String.valueOf(curCh));
        } else if (curCh == ')') {
            t = new Token(TokenType.RPAREN, String.valueOf(curCh));
        } else if (curCh == '=') {
            t = new Token(TokenType.ASSIGN, String.valueOf(curCh));
        } else if (curCh == '\n') {
            t = new Token(TokenType.NEWLINE, String.valueOf(curCh));
        } else {
            if (isAlpha(curCh)) {
                String str = readString();
                if ("let".equals(str)) {
                    return new Token(TokenType.LET, str);
                } else {
                    return new Token(TokenType.ID, str);
                }
            } else if (isDigit(curCh)) {
                Token nt = readNumberToken();
                if (nt != null) {
                    return nt;
                }
            }
        }
        if (t == null) {
            t = new Token(TokenType.ILLEGAL, String.valueOf(curCh));
        }
        readChar();
        return t;
    }

    private char peekCh() {
        return peekIdx == input.length() ? 0 : input.charAt(peekIdx);
    }

    private Token readNumberToken() {
        int start = curIdx;
        while(isDigit(curCh)) {
            readChar();
        }
        if (curCh == '.') {
            readChar();
            while(isDigit(curCh)) {
                readChar();
            }
        }
        int end = curIdx;
        return new Token(TokenType.NUMBER, input.substring(start, end));
    }

    private String readString() {
        int start = curIdx;
        while(isAlpha(curCh) || isDigit(curCh)) {
            readChar();
        }
        int end = curIdx;
        String str = input.substring(start, end);
        return str;
    }

    private boolean isDigit(char ch) {
        return '0' <= ch && ch <= '9';
    }

    private boolean isAlpha(char ch) {
        return ('a' <= ch && ch <= 'z') || ('A' <= ch && ch <= 'Z');
    }

    private void skipSpaces() {
        while(curCh == ' ') {
            readChar();
        }
    }
}
