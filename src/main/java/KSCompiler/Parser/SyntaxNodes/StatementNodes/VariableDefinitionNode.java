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
public class VariableDefinitionNode extends StatementNode {
    private final String datatype;
    private final String identifier;
    private final ExpressionNode expression;

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}