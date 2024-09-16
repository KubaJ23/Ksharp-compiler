package KSCompiler.Parser.SyntaxNodes.FunctionNodes;

import KSCompiler.ASTVisitors.Visitor;
import KSCompiler.ParameterDeclaration;
import KSCompiler.Parser.SyntaxNodes.StatementNode;
import KSCompiler.Parser.SyntaxNodes.StatementNodes.CompoundNode;
import KSCompiler.Parser.SyntaxNodes.SyntaxNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor
public class FunctionNode extends SyntaxNode {
    private final String name;
    private final String returnType;
    private final List<ParameterDeclaration> params;
    private final CompoundNode compoundStatement;

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
