package KSCompiler.Parser.SyntaxNodes.StatementNodes;

import KSCompiler.ASTVisitors.Visitor;
import KSCompiler.Parser.SyntaxNodes.StatementNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor
public class CompoundNode extends StatementNode {
    private final List<StatementNode> nestedStatements;

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
