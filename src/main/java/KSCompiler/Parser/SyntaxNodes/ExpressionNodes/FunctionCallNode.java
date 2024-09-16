package KSCompiler.Parser.SyntaxNodes.ExpressionNodes;

import KSCompiler.ASTVisitors.Visitor;
import KSCompiler.Parser.SyntaxNodes.ExpressionNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor
public class FunctionCallNode extends ExpressionNode {
    private final String name;
    private final List<ExpressionNode> argIdentifiers;

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}