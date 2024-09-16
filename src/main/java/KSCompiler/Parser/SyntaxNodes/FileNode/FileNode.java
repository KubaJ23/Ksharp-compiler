package KSCompiler.Parser.SyntaxNodes.FileNode;

import KSCompiler.ASTVisitors.Visitor;
import KSCompiler.Parser.SyntaxNodes.FunctionNodes.FunctionNode;
import KSCompiler.Parser.SyntaxNodes.SyntaxNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.*;

@Getter
@ToString
@AllArgsConstructor
public class FileNode extends SyntaxNode {
    private final List<FunctionNode> functions;

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
