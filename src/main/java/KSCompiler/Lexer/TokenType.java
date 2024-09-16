package KSCompiler.Lexer;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum TokenType {
    INTCONST(false),
    STRCONST(false),
    KEYWORD(false),
    IDENTIFIER(false),
    EOF(false),
    LCURLY(false),
    RCURLY(false),
    LSQUARE(false),
    RSQUARE(false),
    LPAREN(false),
    RPAREN(false),
    COMMA(false),
    SEMCOL(false),
    COL(false),
    NOT(false),
    ASSIGNMENT(false),

    DIV(true),
    LT(true),
    MT(true),
    PLUS(true),
    MINUS(true),
    MULT(true),
    AND(true),
    BITAND(true),
    BITOR(true),
    NEQ(true),
    EQ(true);

    boolean isBinOperator;

    public static boolean isDataType(String text) {
        return List.of("string", "int").contains(text);
    }

    public static boolean isKeyword(String text) {
        return List.of("if", "while", "return", "void").contains(text) || isDataType(text);
    }
}
