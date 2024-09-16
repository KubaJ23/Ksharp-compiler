package KSCompiler;

import KSCompiler.ASTVisitors.CheckDeclaredVarsVisitor;
import KSCompiler.ASTVisitors.JavaCodeGenVisitor;
import KSCompiler.ASTVisitors.TypeMatchingVisitor;
import KSCompiler.Lexer.Token;
import KSCompiler.Lexer.TokenType;
import KSCompiler.Lexer.Tokenizer;
import KSCompiler.Parser.Parser;
import KSCompiler.Parser.SyntaxNodes.FileNode.FileNode;
import KSCompiler.Parser.SyntaxNodes.SyntaxNode;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Compiler {
    public static String compile(String text, String outputName, Boolean printTextWithoutComments, Boolean printTokens, Boolean printAST, Boolean printSymbolTable, Boolean printOutput) throws Exception {
        Tokenizer tokenizer = new Tokenizer(text);
        List<Token> tokens = new ArrayList<>();

        try {
            tokens = tokenizer.tokenize();
        } catch (Exception e) {
            throw new Exception("Error occured during tokenization:\n%s\n%s\n".formatted(e.getMessage(), Arrays.toString(e.getStackTrace())));
        }
        if (printTextWithoutComments) {
            Global.printLineSeperator();
            System.out.printf("Text without comments: \n%s%s%s\n", Global.ANSI_CYAN, tokenizer.getText(), Global.ANSI_RESET);
        }
        if (printTokens) {
            Global.printLineSeperator();
            printTokens(tokens);
        }


        Parser parser = new Parser(tokens);
        FileNode ASTRoot = null;
        try {
            ASTRoot = parser.parseFile();
        } catch (Exception e) {
            throw new Exception("Error occured during Parsing:\n%s\n%s\n".formatted(e.getMessage(), Arrays.toString(e.getStackTrace())));
        }
        if (printAST) {
            Global.printLineSeperator();
            printAST(ASTRoot, 0);
        }

        SymbolTable symbolTable;
        try {
            symbolTable = semanticAnalysis(ASTRoot);
        } catch (Exception e) {
            throw new Exception("Error occured during semantic analysis:\n%s\n%s\n".formatted(e.getMessage(), Arrays.toString(e.getStackTrace())));
        }

        if (printSymbolTable) {
            Global.printLineSeperator();
            symbolTable.print();
        }

        JavaCodeGenVisitor codeGenerator = new JavaCodeGenVisitor(symbolTable, outputName);
        ASTRoot.accept(codeGenerator);
        if (!codeGenerator.getErrorMessages().isEmpty()) {
            String errors = "";
            for (String msg : codeGenerator.getErrorMessages()) {
                errors += msg;
            }
            throw new Exception("Error occured during semantic analysis:\n" + errors + "\n");
        }

        if (printOutput) {
            Global.printLineSeperator();
            System.out.println(codeGenerator.getOutput());
        }

        return codeGenerator.getOutput();
    }

    private static SymbolTable semanticAnalysis(FileNode astRoot) throws Exception {
        // performs semantic analysis (all types) on the AST tree using visitors/visitor pattern

        CheckDeclaredVarsVisitor checkDeclaredVarsVisitor = new CheckDeclaredVarsVisitor();
        astRoot.accept(checkDeclaredVarsVisitor);

        if (!checkDeclaredVarsVisitor.getErrorMessages().isEmpty()) {
            String errors = "";
            for (String msg : checkDeclaredVarsVisitor.getErrorMessages()) {
                errors += msg;
            }
            throw new Exception(errors);
        }

        TypeMatchingVisitor typeMatchingVisitor = new TypeMatchingVisitor(checkDeclaredVarsVisitor.getSymbolTable());
        astRoot.accept(typeMatchingVisitor);

        if (!typeMatchingVisitor.getErrorMessages().isEmpty()) {
            String errors = "";
            for (String msg : typeMatchingVisitor.getErrorMessages()) {
                errors += msg;
            }
            throw new Exception(errors);
        }
        return checkDeclaredVarsVisitor.getSymbolTable();
    }

    private static void printSymbolTable(SymbolTable symbolTable) {
        symbolTable.getSymbolTable().forEach((entry) -> {
            System.out.printf("Identifier: %15s - isFunction: %-15s, isDeclared: %-15s, Datatype: %-15s\n", entry.getIdentifier(), entry.getIsFunction(), entry.getIsDeclared(), entry.getDataType());
        });
    }

    private static void printTokens(List<Token> tokens) {
        System.out.println("Tokens:");
        for (Token token : tokens) {
            System.out.printf("%15s %s%-10s%s [ line: %s ]\n", "%s".formatted(token.type()), Global.ANSI_GREEN, token.value() == null ? "" : "(" + token.value() + ")", Global.ANSI_RESET, token.lineNum());
        }
    }

    public static void printAST(Object node, int indent) {
        Class<?> clazz = node.getClass();
        Field[] fields = clazz.getDeclaredFields();

        System.out.print("    ".repeat(indent));
        String nodeName = clazz.getSimpleName().toUpperCase();
        if (nodeName.contains("NODE")) nodeName = nodeName.substring(0, nodeName.indexOf("NODE"));
        System.out.printf("| [%s%s%s]\n", Global.ANSI_PURPLE, nodeName, Global.ANSI_RESET);

        for (Field field : fields) {
            field.setAccessible(true); // To access private fields
            try {
                Object value = field.get(node);
                System.out.print("    ".repeat(indent));
                System.out.printf("| %s%s%s", Global.ANSI_GREEN, field.getName(), Global.ANSI_RESET);
                if (isPrimitiveOrWrapper(field.getType())) {
                    System.out.printf(" - " + value);
                }
                if (field.getType() == TokenType.class) {
                    System.out.printf(" - " + value);
                }
                System.out.println();
                if (value != null) {
                    if (SyntaxNode.class.isAssignableFrom(field.getType())) {
                        printAST((SyntaxNode) value, indent + 2);
                    } else if (List.class.isAssignableFrom(field.getType())) {
                        List<?> list = (List<?>) value;
                        for (Object item : list) {
                            if (SyntaxNode.class.isAssignableFrom(item.getClass())) {
                                printAST((SyntaxNode) item, indent + 2);
                            }
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isPrimitiveOrWrapper(Class<?> type) {
        return type.isPrimitive() || type == Boolean.class || type == Character.class || type == Byte.class || type == Short.class || type == Integer.class || type == Long.class || type == Float.class || type == Double.class || type == String.class;
    }
}
