package KSCompiler.ASTVisitors;

import KSCompiler.Lexer.TokenType;
import KSCompiler.ParameterDeclaration;
import KSCompiler.Parser.SyntaxNodes.ExpressionNodes.*;
import KSCompiler.Parser.SyntaxNodes.FileNode.FileNode;
import KSCompiler.Parser.SyntaxNodes.FunctionNodes.FunctionNode;
import KSCompiler.Parser.SyntaxNodes.StatementNodes.*;
import KSCompiler.SymbolTable;
import KSCompiler.SymbolTableEntry;

import java.util.ArrayList;
import java.util.List;

public class TypeMatchingVisitor extends Visitor {
    private String currentFunc;
    private String evaluatedDatatype;

    public TypeMatchingVisitor(SymbolTable table) {
        symbolTable = table;
    }

    @Override
    public void visit(FileNode node) {
        for (int i = 0; i < node.getFunctions().size(); i++) {
            String savedScope = currentScope;
            currentScope += (node.getFunctions().get(i).getName());
            currentFunc = node.getFunctions().get(i).getName();
            node.getFunctions().get(i).accept(this);
            currentScope = savedScope;
        }
    }

    @Override
    public void visit(FunctionNode node) {
        node.getCompoundStatement().accept(this);
    }

    @Override
    public void visit(CompoundNode node) {
        for (int i = 0; i < node.getNestedStatements().size(); i++) {
            if (node.getNestedStatements().get(i) instanceof CompoundNode) {
                String savedScope = currentScope;
                currentScope += i;

                node.getNestedStatements().get(i).accept(this);

                currentScope = savedScope;
            } else {
                node.getNestedStatements().get(i).accept(this);
            }
        }
    }

    @Override
    public void visit(AssignmentStatementNode node) {
        node.getExpression().accept(this);
        if (!evaluatedDatatype.equals(symbolTable.getEntry(node.getIdentifier(), currentScope).getDataType()))
            errorMessages.add("On Line %d, variable datatype doesn't match expression datatype".formatted(node.getLineNumber()));

    }

    @Override
    public void visit(IfNode node) {
        node.getConditionExpression().accept(this);

        if (!evaluatedDatatype.equals("int")) {
            errorMessages.add("On Line %d, expression in 'if' statement does not evaluate to an integer".formatted(node.getLineNumber()));
        }

        node.getBody().accept(this);
    }

    @Override
    public void visit(WhileNode node) {
        node.getConditionExpression().accept(this);

        if (!evaluatedDatatype.equals("int")) {
            errorMessages.add("On Line %d, expression in 'if' statement does not evaluate to an integer".formatted(node.getLineNumber()));
        }

        node.getBody().accept(this);
    }

    @Override
    public void visit(NoOpNode node) {

    }

    @Override
    public void visit(ReturnNode node) {
        SymbolTableEntry func = symbolTable.getEntry(currentFunc, "");

        if (node.getExpression() == null || func.getDataType().equals("void")) {
            if (!(node.getExpression() == null && func.getDataType().equals("void"))) {
                errorMessages.add("On Line %d, in a void function a return statement should not return an expression".formatted(node.getLineNumber(), func.getDataType(), func.getIdentifier()));
                return;
            }
            return;
        }

        node.getExpression().accept(this);
        if (!evaluatedDatatype.equals(func.getDataType()))
            errorMessages.add("On Line %d, datatype of evaluated returned expression does not match return datatype '%s' from function '%s'".formatted(node.getLineNumber(), func.getDataType(), func.getIdentifier()));

    }

    @Override
    public void visit(VariableDefinitionNode node) {
        if (node.getExpression() != null) {
            node.getExpression().accept(this);
            if (!evaluatedDatatype.equals(node.getDatatype()))
                errorMessages.add("On Line %d, datatype of evaluated expression does not match datatype of declared variable".formatted(node.getLineNumber()));
        }
    }

    @Override
    public void visit(BinExprNode node) {
        node.getLeftExpr().accept(this);
        String leftDatatype = evaluatedDatatype;

        node.getRightExpr().accept(this);
        String rightDatatype = evaluatedDatatype;

        node.leftDatatype = leftDatatype;
        node.rightDatatype = rightDatatype;

        if ("string".equals(leftDatatype) && "string".equals(rightDatatype)) {
            if (node.getOperator() == TokenType.PLUS) {
                this.evaluatedDatatype = "string";
            } else if (node.getOperator() == TokenType.EQ || node.getOperator() == TokenType.NEQ) {
                this.evaluatedDatatype = "int";
            } else {
                errorMessages.add("On Line %d, Invalid operator between strings".formatted(node.getLineNumber()));
                this.evaluatedDatatype = "";
            }
        } else if (("int".equals(leftDatatype) && "string".equals(rightDatatype)) || ("string".equals(leftDatatype) && "int".equals(rightDatatype))) {
            if (node.getOperator() != TokenType.PLUS) {
                errorMessages.add("On Line %d, Invalid operator between string and an integer".formatted(node.getLineNumber()));
                this.evaluatedDatatype = "";
            } else {
                this.evaluatedDatatype = "string";
            }
        } else if ("int".equals(leftDatatype) && "int".equals(rightDatatype)) {
            // any binary operator is valid for 2 integers
            this.evaluatedDatatype = "int";
        }
    }

    @Override
    public void visit(FunctionCallNode node) {
        List<ParameterDeclaration> parameters = symbolTable.getEntry(node.getName(), currentScope).getInputParameters();

        // check correct number of arguments given
        if (parameters.size() != node.getArgIdentifiers().size()) {
            errorMessages.add("On Line %d, Number of arguments doesn't match function definition".formatted(node.getLineNumber()));
            return;
        }

        for (int i = 0; i < node.getArgIdentifiers().size(); i++) {
            node.getArgIdentifiers().get(i).accept(this);
            if (!evaluatedDatatype.equals(parameters.get(i).dataType())) {
                errorMessages.add("On Line %d, given argument '%s' doesn't match expected parameter '%s'".formatted(node.getLineNumber(), evaluatedDatatype, parameters.get(i).dataType()));
            }
        }

        evaluatedDatatype = symbolTable.getEntry(node.getName(), currentScope).getDataType();
    }

    @Override
    public void visit(IdentifierNode node) {
        evaluatedDatatype = symbolTable.getEntry(node.getName(), currentScope).getDataType();
    }

    @Override
    public void visit(IntegerLiteralNode node) {
        evaluatedDatatype = "int";
    }

    @Override
    public void visit(StringLiteralNode node) {
        evaluatedDatatype = "string";
    }

    @Override
    public void visit(UnaryExprNode node) {
        node.getExpression().accept(this);
        if (evaluatedDatatype != "int") {
            errorMessages.add("On Line %d, unary operator is performed on non-integer value".formatted(node.getLineNumber()));
        } else {
            evaluatedDatatype = "int";
        }
    }

    @Override
    public void visit(IndexingExprNode node) {
        node.getLeftExpr().accept(this);
        String leftDatatype = evaluatedDatatype;

        node.getIndex().accept(this);
        String indexDatatype = evaluatedDatatype;

        if (!leftDatatype.equals("string")) {
            errorMessages.add("On Line %d, indexing can only be performed on a string".formatted(node.getLineNumber()));
        }
        if (!indexDatatype.equals("int")) {
            errorMessages.add("On Line %d, index expression must evaluate to an integer".formatted(node.getLineNumber()));
        }

        evaluatedDatatype = leftDatatype;
    }

    @Override
    public void visit(FunctionCallStatementNode node) {
        node.getFunction().accept(this);
    }
}
