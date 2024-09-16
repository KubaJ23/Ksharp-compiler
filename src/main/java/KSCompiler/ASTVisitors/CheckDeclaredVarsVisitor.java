package KSCompiler.ASTVisitors;

import KSCompiler.ParameterDeclaration;
import KSCompiler.Parser.SyntaxNodes.ExpressionNode;
import KSCompiler.Parser.SyntaxNodes.ExpressionNodes.*;
import KSCompiler.Parser.SyntaxNodes.FileNode.FileNode;
import KSCompiler.Parser.SyntaxNodes.FunctionNodes.FunctionNode;
import KSCompiler.Parser.SyntaxNodes.StatementNodes.*;
import KSCompiler.SymbolTable;
import KSCompiler.SymbolTableEntry;

import java.util.ArrayList;
import java.util.List;

public class CheckDeclaredVarsVisitor extends Visitor {
    public CheckDeclaredVarsVisitor() {
        symbolTable = new SymbolTable();
        symbolTable.add(new SymbolTableEntry(true, true, true, "void", "print", "", List.of(new ParameterDeclaration("string", "text"))));
        symbolTable.add(new SymbolTableEntry(true, true, true, "string", "read", "", new ArrayList<>()));
        symbolTable.add(new SymbolTableEntry(true, true, true, "int", "length", "", List.of(new ParameterDeclaration("string", "text"))));
    }

    @Override
    public void visit(FileNode node) {
        // add all functions in the file to the symbol table
        for (FunctionNode function : node.getFunctions()) {
            SymbolTableEntry entry = symbolTable.add(new SymbolTableEntry(true, true, true, function.getReturnType(), function.getName(), currentScope, function.getParams()));
            if (entry == null) { // function with same name exists already
                errorMessages.add("On Line %d, Function with the same identifier already exists".formatted(function.getLineNumber()));
            }
        }

        for (int i = 0; i < node.getFunctions().size(); i++) {
            String savedScope = currentScope;
            currentScope += (node.getFunctions().get(i).getName());
            node.getFunctions().get(i).accept(this);
            currentScope = savedScope;
        }
    }

    @Override
    public void visit(FunctionNode node) {
        for (ParameterDeclaration param : node.getParams()) {
            if (symbolTable.getEntry(param.name(), currentScope) != null) {
                errorMessages.add("On Line %d, variable with identifier '%s' has already been defined in this scope".formatted(node.getLineNumber(), param.name()));
            } else {
                symbolTable.add(new SymbolTableEntry(false, true, true, param.dataType(), param.name(), currentScope, null));
            }
        }

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
        if (symbolTable.getEntry(node.getIdentifier(), currentScope) == null)
            errorMessages.add("On Line %d, assignment is being made to an undeclared variable '%s'".formatted(node.getLineNumber(), node.getIdentifier()));
        else symbolTable.getEntry(node.getIdentifier(), currentScope).setInitialised(true);
    }

    @Override
    public void visit(IfNode node) {
        node.getConditionExpression().accept(this);
        node.getBody().accept(this);
    }

    @Override
    public void visit(WhileNode node) {
        node.getConditionExpression().accept(this);
        node.getBody().accept(this);
    }

    @Override
    public void visit(NoOpNode node) {
    }

    @Override
    public void visit(ReturnNode node) {
        if (node.getExpression() != null) node.getExpression().accept(this);
    }

    @Override
    public void visit(VariableDefinitionNode node) {
        Boolean isInitialised = false;
        if (node.getExpression() != null) {
            isInitialised = true;
            node.getExpression().accept(this);
        }
        if (symbolTable.getEntry(node.getIdentifier(), currentScope) != null) {
            errorMessages.add("On Line %d, variable with identifier '%s' has already been defined in this scope".formatted(node.getLineNumber(), node.getIdentifier()));
        } else {
            symbolTable.add(new SymbolTableEntry(false, true, isInitialised, node.getDatatype(), node.getIdentifier(), currentScope, null));
        }
    }

    @Override
    public void visit(BinExprNode node) {
        node.getLeftExpr().accept(this);
        node.getRightExpr().accept(this);
    }

    @Override
    public void visit(FunctionCallNode node) {
        if (symbolTable.getEntry(node.getName(), currentScope) == null) {
            errorMessages.add("On Line %d, Function with identifier '%s' not defined".formatted(node.getLineNumber(), node.getName()));
        }

        for (ExpressionNode expr : node.getArgIdentifiers()) {
            expr.accept(this);
        }
    }

    @Override
    public void visit(IdentifierNode node) {
        if (symbolTable.getEntry(node.getName(), currentScope) == null) {
            errorMessages.add("On Line %d, variable with identifier '%s' not defined".formatted(node.getLineNumber(), node.getName()));
        } else if (!symbolTable.getEntry(node.getName(), currentScope).getIsInitialised()) {
            errorMessages.add("On Line %d, variable with identifier '%s' has not been initialised".formatted(node.getLineNumber(), node.getName()));
        }
    }

    @Override
    public void visit(IntegerLiteralNode node) {

    }

    @Override
    public void visit(StringLiteralNode node) {

    }

    @Override
    public void visit(UnaryExprNode node) {
        node.getExpression().accept(this);
    }

    @Override
    public void visit(IndexingExprNode node) {
        node.getLeftExpr().accept(this);
        node.getIndex().accept(this);
    }

    @Override
    public void visit(FunctionCallStatementNode node) {
        node.getFunction().accept(this);
    }
}
