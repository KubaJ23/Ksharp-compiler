package KSCompiler.ASTVisitors;

import KSCompiler.Parser.SyntaxNodes.ExpressionNodes.*;
import KSCompiler.Parser.SyntaxNodes.FileNode.FileNode;
import KSCompiler.Parser.SyntaxNodes.FunctionNodes.FunctionNode;
import KSCompiler.Parser.SyntaxNodes.StatementNodes.*;
import KSCompiler.SymbolTable;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class Visitor {
    protected SymbolTable symbolTable;
    protected String currentScope = "";
    protected List<String> errorMessages = new ArrayList<>();

    public abstract void visit(FileNode node);


    public abstract void visit(FunctionNode node);


    public abstract void visit(CompoundNode node);

    public abstract void visit(AssignmentStatementNode node);

    public abstract void visit(IfNode node);

    public abstract void visit(WhileNode node);

    public abstract void visit(NoOpNode node);

    public abstract void visit(ReturnNode node);

    public abstract void visit(VariableDefinitionNode node);


    public abstract void visit(BinExprNode node);

    public abstract void visit(FunctionCallNode node);

    public abstract void visit(IdentifierNode node);

    public abstract void visit(IntegerLiteralNode node);

    public abstract void visit(StringLiteralNode node);

    public abstract void visit(UnaryExprNode node);

    public abstract void visit(IndexingExprNode node);

    public abstract void visit(FunctionCallStatementNode node);
}
