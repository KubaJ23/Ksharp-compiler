package KSCompiler.Parser.SyntaxNodes.StatementNodes;

import KSCompiler.ASTVisitors.Visitor;
import KSCompiler.Lexer.TokenType;
import KSCompiler.Parser.SyntaxNodes.ExpressionNode;
import KSCompiler.Parser.SyntaxNodes.StatementNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class AssignmentStatementNode extends StatementNode {
    private final String identifier;
    private final TokenType operator;
    private final ExpressionNode expression;

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}