package KSCompiler.Parser.SyntaxNodes.ExpressionNodes;

import KSCompiler.ASTVisitors.Visitor;
import KSCompiler.Parser.SyntaxNodes.ExpressionNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class IntegerLiteralNode extends ExpressionNode {
    private final int value;

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}