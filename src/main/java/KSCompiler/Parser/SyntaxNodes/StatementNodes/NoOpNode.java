package KSCompiler.Parser.SyntaxNodes.StatementNodes;

import KSCompiler.ASTVisitors.Visitor;
import KSCompiler.Parser.SyntaxNodes.StatementNode;

public class NoOpNode extends StatementNode {
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
