package KSCompiler.ASTVisitors;

import KSCompiler.Lexer.TokenType;
import KSCompiler.ParameterDeclaration;
import KSCompiler.Parser.SyntaxNodes.ExpressionNode;
import KSCompiler.Parser.SyntaxNodes.ExpressionNodes.*;
import KSCompiler.Parser.SyntaxNodes.FileNode.FileNode;
import KSCompiler.Parser.SyntaxNodes.FunctionNodes.FunctionNode;
import KSCompiler.Parser.SyntaxNodes.StatementNode;
import KSCompiler.Parser.SyntaxNodes.StatementNodes.*;
import KSCompiler.SymbolTable;
import KSCompiler.SymbolTableEntry;
import lombok.Getter;

@Getter
public class JavaCodeGenVisitor extends Visitor {
    private String filename;
    private String output;

    public JavaCodeGenVisitor(SymbolTable symbolTable, String filename) {
        this.filename = filename;
        this.symbolTable = symbolTable;
    }

    private String translateDatatype(String datatype) {
        switch (datatype) {
            case "string":
                return "String";
            case "int":
                return "int";
            case "void":
                return "void";
        }
        return "UNKNOWN_DATATYPE";
    }

    @Override
    public void visit(FileNode node) {
        SymbolTableEntry mainFunc = symbolTable.getEntry("main", "");
        if (mainFunc == null) {
            errorMessages.add("Error - no 'main' function found for entry point to program");
            return;
        }
        if (!"void".equals(mainFunc.getDataType())) {
            errorMessages.add("Error - return datatype of entry point function must be 'void'");
            return;
        }
        if (mainFunc.getInputParameters().size() != 0) {
            errorMessages.add("Error - entry point function 'main' must have no parameters");
            return;
        }
        StringBuilder code = new StringBuilder();

        code.append("import java.util.Scanner;");
        code.append("public class %s {".formatted(filename));

        node.getFunctions().forEach(functionNode -> {
            functionNode.accept(this);
            code.append(output);
        });

        code.append("public static void print(String text){System.out.print(text);}");
        code.append("public static String read(){Scanner reader = new Scanner(System.in); return reader.nextLine();}");
        code.append("public static int length(String text){return text.length();}");

        code.append("}");

        output = code.toString();
    }

    @Override
    public void visit(FunctionNode node) {
        StringBuilder code = new StringBuilder();

        if ("main".equals(node.getName())) {
            code.append("public static void main (String args[])");
            node.getCompoundStatement().accept(this);
            code.append(output);
        } else {
            code.append("public static %s %s (".formatted(translateDatatype(node.getReturnType()), node.getName()));
            if (!node.getParams().isEmpty()) {
                ParameterDeclaration param = node.getParams().getFirst();
                code.append("%s %s".formatted(translateDatatype(param.dataType()), param.name()));
            }
            if (node.getParams().size() > 1) {
                for (int i = 1; i < node.getParams().size(); i++) {
                    ParameterDeclaration param = node.getParams().get(i);
                    code.append(",%s %s".formatted(translateDatatype(param.dataType()), param.name()));
                }
            }
            code.append(")");
            node.getCompoundStatement().accept(this);
            code.append(output);
        }
        output = code.toString();
    }

    @Override
    public void visit(CompoundNode node) {
        StringBuilder code = new StringBuilder();
        code.append("{");
        for (StatementNode statement : node.getNestedStatements()) {
            statement.accept(this);
            code.append(output);
        }
        code.append("}");
        output = code.toString();
    }

    @Override
    public void visit(AssignmentStatementNode node) {
        StringBuilder code = new StringBuilder();
        code.append(node.getIdentifier()).append("=");
        node.getExpression().accept(this);
        code.append(output + ';');
        output = code.toString();
    }

    @Override
    public void visit(IfNode node) {
        StringBuilder code = new StringBuilder();

        code.append("if (");
        node.getConditionExpression().accept(this);
        code.append(output);
        code.append("!=0)");

        node.getBody().accept(this);
        code.append(output);

        output = code.toString();
    }

    @Override
    public void visit(WhileNode node) {
        StringBuilder code = new StringBuilder();

        code.append("while (");
        node.getConditionExpression().accept(this);
        code.append(output);
        code.append("!=0)");

        node.getBody().accept(this);
        code.append(output);

        output = code.toString();
    }

    @Override
    public void visit(NoOpNode node) {
        StringBuilder code = new StringBuilder();
        code.append(";");
        output = code.toString();
    }

    @Override
    public void visit(ReturnNode node) {
        StringBuilder code = new StringBuilder();

        code.append("return");
        if (node.getExpression() != null) {
            node.getExpression().accept(this);
            code.append(" " + output);
        }
        code.append(";");

        output = code.toString();
    }

    @Override
    public void visit(VariableDefinitionNode node) {
        StringBuilder code = new StringBuilder();

        code.append(translateDatatype(node.getDatatype()) + " ");
        code.append(node.getIdentifier());
        if (node.getExpression() != null) {
            code.append("=");
            node.getExpression().accept(this);
            code.append(output);
        }
        code.append(";");

        output = code.toString();
    }

    @Override
    public void visit(BinExprNode node) {
        StringBuilder code = new StringBuilder();

        node.getLeftExpr().accept(this);
        String leftDatatype = output;
        node.getRightExpr().accept(this);
        String rightDatatype = output;

        code.append("(");
        switch (node.getOperator()) {
            case DIV:
                code.append("%s/%s".formatted(leftDatatype, rightDatatype));
                break;
            case LT:
                code.append("%s<%s?1:0".formatted(leftDatatype, rightDatatype));
                break;
            case MT:
                code.append("%s>%s?1:0".formatted(leftDatatype, rightDatatype));
                break;
            case PLUS:
                code.append("%s+%s".formatted(leftDatatype, rightDatatype));
                break;
            case MINUS:
                code.append("%s-%s".formatted(leftDatatype, rightDatatype));
                break;
            case MULT:
                code.append("%s*%s".formatted(leftDatatype, rightDatatype));
                break;
            case AND:
                code.append("(%s != 0)&&(%s != 0)?1:0".formatted(leftDatatype, rightDatatype));
                break;
            case BITAND:
                code.append("%s&%s".formatted(leftDatatype, rightDatatype));
                break;
            case BITOR:
                code.append("%s|%s".formatted(leftDatatype, rightDatatype));
                break;
            case NEQ:
                if (node.leftDatatype.equals("string") && node.rightDatatype.equals("string")) { // both sides of binary expression must be strings if one is a string and operator is NEQ
                    code.append("%s.equals(%s)?0:1".formatted(leftDatatype, rightDatatype));
                } else {
                    code.append("%s!=%s?1:0".formatted(leftDatatype, rightDatatype));
                }
                break;
            case EQ:
                if (node.leftDatatype.equals("string") && node.rightDatatype.equals("string")) { // both sides of binary expression must be strings if one is a string and operator is EQ
                    code.append("%s.equals(%s)?1:0".formatted(leftDatatype, rightDatatype));
                } else {
                    code.append("%s==%s?1:0".formatted(leftDatatype, rightDatatype));
                }
                break;
        }
        code.append(")");

        output = code.toString();
    }

    @Override
    public void visit(FunctionCallNode node) {
        StringBuilder code = new StringBuilder();

        code.append(node.getName());
        code.append("(");

        if (!node.getArgIdentifiers().isEmpty()) {
            node.getArgIdentifiers().getFirst().accept(this);
            code.append(output);
        }
        if (node.getArgIdentifiers().size() > 1) {
            for (int i = 1; i < node.getArgIdentifiers().size(); i++) {
                node.getArgIdentifiers().get(i).accept(this);
                code.append("," + output);
            }
        }

        code.append(")");
        output = code.toString();
    }

    @Override
    public void visit(IdentifierNode node) {
        output = node.getName();
    }

    @Override
    public void visit(IntegerLiteralNode node) {
        output = "" + node.getValue();
    }

    @Override
    public void visit(StringLiteralNode node) {
        output = "\"" + node.getValue() + "\"";
    }

    @Override
    public void visit(UnaryExprNode node) {
        StringBuilder code = new StringBuilder();

        node.getExpression().accept(this);
        if (node.getOperator() == TokenType.MINUS) {
            code.append("-" + output);
        } else if (node.getOperator() == TokenType.NOT) {
            code.append("~" + output);
        }

        output = code.toString();
    }

    @Override
    public void visit(IndexingExprNode node) {
        StringBuilder code = new StringBuilder();

        node.getLeftExpr().accept(this);
        String leftexpr = output;
        node.getIndex().accept(this);
        code.append("%s.substring(%s,%s + 1)".formatted(leftexpr, output, output));

        output = code.toString();
    }

    @Override
    public void visit(FunctionCallStatementNode node) {
        StringBuilder code = new StringBuilder();
        node.getFunction().accept(this);
        output = output + ';';
    }
}
