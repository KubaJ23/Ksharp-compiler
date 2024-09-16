package KSCompiler.Parser.SyntaxNodes;

import KSCompiler.ASTVisitors.Visitor;
import lombok.Getter;
import lombok.Setter;

@Getter
public abstract class SyntaxNode {
    private int lineNumber;

    public SyntaxNode setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
        return this;
    }

    public abstract void accept(Visitor visitor);
}
