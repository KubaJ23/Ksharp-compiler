package KSCompiler.Parser.SyntaxNodes.StatementNodes;

import KSCompiler.ASTVisitors.Visitor;
import KSCompiler.Parser.SyntaxNodes.ExpressionNodes.FunctionCallNode;
import KSCompiler.Parser.SyntaxNodes.StatementNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class FunctionCallStatementNode extends StatementNode {
    private final FunctionCallNode function;

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}