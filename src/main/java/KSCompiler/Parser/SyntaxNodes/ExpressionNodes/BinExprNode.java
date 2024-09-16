package KSCompiler.Parser.SyntaxNodes.ExpressionNodes;

import KSCompiler.ASTVisitors.Visitor;
import KSCompiler.Lexer.TokenType;
import KSCompiler.Parser.SyntaxNodes.ExpressionNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class BinExprNode extends ExpressionNode {
    private final TokenType operator;
    private final ExpressionNode leftExpr;
    private final ExpressionNode rightExpr;
    public String leftDatatype;
    public String rightDatatype;

    public BinExprNode(TokenType operator, ExpressionNode leftExpr, ExpressionNode rightExpr) {
        this.operator = operator;
        this.leftExpr = leftExpr;
        this.rightExpr = rightExpr;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}