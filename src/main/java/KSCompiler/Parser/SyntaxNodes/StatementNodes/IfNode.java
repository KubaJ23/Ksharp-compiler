package KSCompiler.Parser.SyntaxNodes.StatementNodes;

import KSCompiler.ASTVisitors.Visitor;
import KSCompiler.Parser.SyntaxNodes.ExpressionNode;
import KSCompiler.Parser.SyntaxNodes.StatementNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class IfNode extends StatementNode {
    private final ExpressionNode conditionExpression;
    private final CompoundNode body;

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
