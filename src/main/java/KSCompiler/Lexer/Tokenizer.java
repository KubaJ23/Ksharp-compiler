package KSCompiler.Lexer;

import KSCompiler.Global;
import KSCompiler.SymbolTable;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

import static KSCompiler.Lexer.TokenType.isDataType;
import static KSCompiler.Lexer.TokenType.isKeyword;

@Getter
public class Tokenizer {
    private int currentLineNum = 1;
    private String text;
    private int current;

    public Tokenizer(String text) {
        this.text = text;
    }

    public List<Token> tokenize() throws Exception {
        // remove comments
        text = withoutComments();

        // get tokens
        ArrayList<Token> tokens = new ArrayList<>();
        current = 0;

        while (peek() != null) {
            char chr = consume();

            // pass over whitespace
            if (Character.isWhitespace(chr)) {
                if (chr == '\n') currentLineNum++;
                continue;
            }

            // for string literals
            if ('"' == chr) {
                StringBuilder buffer = new StringBuilder();
                while (!"\"".equals(peek())) {
                    if (peek() == null) {
                        throw new Exception("Syntax Error - String literal doesn't have an ending [Line %s]".formatted(currentLineNum));
                    }
                    buffer.append(consume());
                }
                consume();
                tokens.add(new Token(TokenType.STRCONST, buffer.toString(), currentLineNum));
                continue;
            }

            // for various symbol like tokens
            if (!Character.isLetterOrDigit(chr)) {
                tokens.add(matchForSymbols(chr));
                continue;
            }

            // for integer literals
            if (Character.isDigit(chr)) {
                StringBuilder buffer = new StringBuilder();

                buffer.append(chr);
                while (peek() != null) {
                    if (!Character.isDigit(peek().charAt(0))) {
                        break;
                    }
                    buffer.append(consume());
                }
                tokens.add(new Token(TokenType.INTCONST, buffer.toString(), currentLineNum));
                continue;
            }

            // for keywords and indetifiers
            if (Character.isLetter(chr)) {
                StringBuilder buffer = new StringBuilder().append(chr);

                while (peek() != null && Character.isLetter(peek().charAt(0))) {
                    buffer.append(consume());
                }
                String letterRun = buffer.toString();

                if (isKeyword(letterRun) &&
                        (peek() == null || !Character.isLetterOrDigit(peek().charAt(0)))) {
                    // must be a keyword
                    tokens.add(new Token(TokenType.KEYWORD, letterRun, currentLineNum));
                    continue;
                }

                // must be an identifier...
                while (peek() != null && Character.isLetterOrDigit(peek().charAt(0))) {
                    buffer.append(consume());
                }

                tokens.add(new Token(TokenType.IDENTIFIER, buffer.toString(), currentLineNum));
            }
        }
        tokens.add(new Token(TokenType.EOF, null, currentLineNum));

        return tokens;
    }

    private Token matchForSymbols(char chr) throws Exception {
        switch (chr) {
            case '(':
                return (new Token(TokenType.LPAREN, null, currentLineNum));
            case ')':
                return (new Token(TokenType.RPAREN, null, currentLineNum));
            case '{':
                return (new Token(TokenType.LCURLY, null, currentLineNum));
            case '}':
                return (new Token(TokenType.RCURLY, null, currentLineNum));
            case '[':
                return (new Token(TokenType.LSQUARE, null, currentLineNum));
            case ']':
                return (new Token(TokenType.RSQUARE, null, currentLineNum));
            case ',':
                return (new Token(TokenType.COMMA, null, currentLineNum));
            case ';':
                return (new Token(TokenType.SEMCOL, null, currentLineNum));
            case ':':
                return (new Token(TokenType.COL, null, currentLineNum));
            case '*':
                return (new Token(TokenType.MULT, null, currentLineNum));
            case '+':
                return (new Token(TokenType.PLUS, null, currentLineNum));
            case '-':
                return (new Token(TokenType.MINUS, null, currentLineNum));
            case '&':
                if ("&".equals(peek())) {
                    consume();
                    return (new Token(TokenType.AND, null, currentLineNum));
                } else {
                    return (new Token(TokenType.BITAND, null, currentLineNum));
                }
            case '|':
                return (new Token(TokenType.BITOR, null, currentLineNum));
            case '=':
                if ("=".equals(peek())) {
                    consume();
                    return (new Token(TokenType.EQ, null, currentLineNum));
                } else {
                    return (new Token(TokenType.ASSIGNMENT, null, currentLineNum));
                }
            case '!':
                if ("=".equals(peek())) {
                    consume();
                    return (new Token(TokenType.NEQ, null, currentLineNum));
                } else {
                    return (new Token(TokenType.NOT, null, currentLineNum));
                }
            case '<':
                return new Token(TokenType.LT, null, currentLineNum);
            case '>':
                return new Token(TokenType.MT, null, currentLineNum);
            case '/':
                return new Token(TokenType.DIV, null, currentLineNum);
            default:
                throw new Exception("Syntax Error, unrecognised character [Line %s]".formatted(currentLineNum));
        }
    }

    private String withoutComments() {
        String[] lines = text.split("//");
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i < lines.length; i++) {
            int endln = lines[i].indexOf('\n');
            if (endln >= 0) {
                String nonComments = lines[i].substring(endln - 1);
                builder.append(nonComments);
            }
        }

        // remove empty lines
//        return builder.toString().replaceAll("(\r\n)+", System.lineSeparator());

        return builder.toString();
    }

    private String peek() {
        try {
            return "" + text.charAt(current);
        } catch (Exception e) {
            return null;
        }
    }

    private String peek(int charsAhead) {
        try {
            return "" + text.charAt(current + charsAhead);
        } catch (Exception e) {
            return null;
        }
    }

    private char consume() {
        return text.charAt(current++);
    }
}
