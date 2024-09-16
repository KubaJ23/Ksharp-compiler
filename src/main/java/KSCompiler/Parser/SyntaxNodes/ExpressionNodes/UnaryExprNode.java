package KSCompiler.Parser.SyntaxNodes.ExpressionNodes;

import KSCompiler.ASTVisitors.Visitor;
import KSCompiler.Lexer.TokenType;
import KSCompiler.Parser.SyntaxNodes.ExpressionNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class UnaryExprNode extends ExpressionNode {
    private final TokenType operator;
    private final ExpressionNode expression;

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}