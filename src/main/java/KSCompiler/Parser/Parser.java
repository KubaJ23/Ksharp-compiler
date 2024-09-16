package KSCompiler.Parser;

import KSCompiler.Lexer.Token;
import KSCompiler.Lexer.*;
import KSCompiler.ParameterDeclaration;
import KSCompiler.Parser.SyntaxNodes.ExpressionNodes.*;
import KSCompiler.Parser.SyntaxNodes.FileNode.FileNode;
import KSCompiler.Parser.SyntaxNodes.FunctionNodes.FunctionNode;
import KSCompiler.Parser.SyntaxNodes.StatementNodes.*;
import KSCompiler.Parser.SyntaxNodes.*;

import java.util.ArrayList;
import java.util.List;

import static KSCompiler.Lexer.TokenType.*;

@SuppressWarnings("")
public class Parser {
    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public FileNode parseFile() throws Exception {
        List<FunctionNode> functions = new ArrayList<>();
        while (peek() != null && peek().type() != EOF) {
            FunctionNode matchedFunction = matchFunction();
            functions.add(matchedFunction);
        }
        if (!match(EOF)) throw new Exception("EOF was not found at end of file");

        return new FileNode(functions);
    }

    private FunctionNode matchFunction() throws Exception {
        final int startIndex = current;

        if (!match(KEYWORD, IDENTIFIER, COL)) {
            throw new Exception("Error parsing - couldn't match function definition " + getLineNumberStatement(setCurrentToken(startIndex)));
        }
        if (!(TokenType.isDataType(peek().value()) || peek().value().equals("void"))) {
            throw new Exception("Error parsing - illegal function return type " + getLineNumberStatement(setCurrentToken(startIndex)));
        }
        String returnType = consume().value();
        String functionName = consume().value();
        consume(); // ignore colon
        // match parameters
        List<ParameterDeclaration> paramDeclarations = new ArrayList<>();
        if (match(KEYWORD, IDENTIFIER)) {
            if (!TokenType.isDataType(peek().value())) {
                throw new Exception("Error parsing - couldn't match function parameter definitions " + getLineNumberStatement(setCurrentToken(startIndex)));
            }
            String datatype = consume().value();
            String paramName = consume().value();

            paramDeclarations.add(new ParameterDeclaration(datatype, paramName));

            while (match(COMMA, KEYWORD, IDENTIFIER)) {
                consume(); // ignore comma
                if (!TokenType.isDataType(peek().value())) {
                    throw new Exception("Error parsing - couldn't match function parameter definitions " + getLineNumberStatement(setCurrentToken(startIndex)));
                }
                datatype = consume().value();
                paramName = consume().value();
                paramDeclarations.add(new ParameterDeclaration(datatype, paramName));
            }
        }

        CompoundNode statement;
        try {
            statement = matchCompoundStatement();
        } catch (Exception e) {
            setCurrentToken(startIndex);
            throw e;
        }
        return (FunctionNode) new FunctionNode(functionName, returnType, paramDeclarations, statement).setLineNumber(tokens.get(startIndex).lineNum());
    }

    private StatementNode matchStatement() throws Exception {
        try {
            return matchCompoundStatement();
        } catch (Exception _) {
        }
        try {
            return matchIfStatement();
        } catch (Exception _) {
        }
        try {
            return matchWhileStatement();
        } catch (Exception _) {
        }
        try {
            return matchReturnStatement();
        } catch (Exception _) {
        }
        try {
            return matchVarDefStatement();
        } catch (Exception _) {
        }
        try {
            return matchAssignmentStatement();
        } catch (Exception _) {
        }
        try {
            return matchFuncCallStatement();
        } catch (Exception _) {
        }
        try {
            return matchNoOpStatement();
        } catch (Exception _) {
        }

        throw new Exception("Error parsing - couldn't match statement " + getLineNumberStatement(current));
    }

    private FunctionCallStatementNode matchFuncCallStatement() throws Exception {
        final int startIndex = current;

        FunctionCallNode funcCallExpr = null;
        try {
            funcCallExpr = matchFunctionCallExpression();
        } catch (Exception e) {
            setCurrentToken(startIndex);
            throw e;
        }
        if (!match(SEMCOL))
            throw new Exception("Error parsing - couldn't match function call statement, missing semicolon" + getLineNumberStatement(setCurrentToken(startIndex)));
        consume();
        return (FunctionCallStatementNode) new FunctionCallStatementNode(funcCallExpr).setLineNumber(tokens.get(startIndex).lineNum());
    }

    private NoOpNode matchNoOpStatement() throws Exception {
        if (match(SEMCOL)) return (NoOpNode) new NoOpNode().setLineNumber(tokens.get(current).lineNum());
        throw new Exception("Error parsing - couldn't match No-Op statement" + getLineNumberStatement(current));
    }

    private AssignmentStatementNode matchAssignmentStatement() throws Exception {
        final int startIndex = current;

        if (!match(IDENTIFIER, ASSIGNMENT))
            throw new Exception("Error parsing - couldn't match assignment statement, must be an assignment to an identifier" + getLineNumberStatement(setCurrentToken(startIndex)));
        String identifier = consume().value();
        TokenType operator = consume().type();

        ExpressionNode expression = null;
        try {
            expression = matchExpression();
        } catch (Exception e) {
            setCurrentToken(startIndex);
            throw e;
        }

        if (!match(SEMCOL))
            throw new Exception("Error parsing - couldn't match assignment statement, missing semicolon" + getLineNumberStatement(setCurrentToken(startIndex)));
        consume();
        return (AssignmentStatementNode) new AssignmentStatementNode(identifier, operator, expression).setLineNumber(tokens.get(startIndex).lineNum());
    }

    private VariableDefinitionNode matchVarDefStatement() throws Exception {
        final int startIndex = current;

        if (!match(KEYWORD, IDENTIFIER)) {
            throw new Exception("Error parsing - couldn't match variable definition " + getLineNumberStatement(setCurrentToken(startIndex)));
        }

        if (!TokenType.isDataType(peek().value())) {
            throw new Exception("Error parsing - invalid datatype for variable declaration " + getLineNumberStatement(setCurrentToken(startIndex)));
        }
        String datatype = consume().value();

        String variableName = consume().value();

        ExpressionNode expression = null;
        if (match(ASSIGNMENT)) {
            try {
                consume();
                expression = matchExpression();
            } catch (Exception e) {
                setCurrentToken(startIndex);
                throw e;
            }
        }

        if (!match(SEMCOL))
            throw new Exception("Error parsing - missing semicolon " + getLineNumberStatement(setCurrentToken(startIndex)));
        consume();
        return (VariableDefinitionNode) new VariableDefinitionNode(datatype, variableName, expression).setLineNumber(tokens.get(startIndex).lineNum());
    }

    private ReturnNode matchReturnStatement() throws Exception {
        final int startIndex = current;

        if (!(match(KEYWORD) && "return".equals(peek().value()))) {
            throw new Exception("Error parsing - missing return keyword " + getLineNumberStatement(setCurrentToken(startIndex)));
        }
        consume();

        if (match(SEMCOL)) {
            consume();
            return (ReturnNode) new ReturnNode(null).setLineNumber(tokens.get(startIndex).lineNum());
        }

        ExpressionNode expression;
        try {
            expression = matchExpression();
        } catch (Exception e) {
            setCurrentToken(startIndex);
            throw e;
        }

        if (!match(SEMCOL)) {
            throw new Exception("Error parsing - couldn't match 'return' statement, missing semicolon " + getLineNumberStatement(setCurrentToken(startIndex)));
        }
        consume();
        return (ReturnNode) new ReturnNode(expression).setLineNumber(tokens.get(startIndex).lineNum());
    }

    private WhileNode matchWhileStatement() throws Exception {
        final int startIndex = current;

        if (!(match(KEYWORD, LPAREN) && "while".equals(peek().value()))) {
            throw new Exception("Error parsing - couldn't match 'while' statement " + getLineNumberStatement(setCurrentToken(startIndex)));
        }
        consume(2);

        ExpressionNode expression;
        try {
            expression = matchExpression();
        } catch (Exception e) {
            setCurrentToken(startIndex);
            throw e;
        }

        if (!match(RPAREN)) {
            throw new Exception("Error parsing - couldn't match 'while' statement, missing right parenthesis " + getLineNumberStatement(setCurrentToken(startIndex)));
        }
        consume(); // ignore right parenthesis

        CompoundNode body;
        try {
            body = matchCompoundStatement();
        } catch (Exception e) {
            setCurrentToken(startIndex);
            throw e;
        }

        return (WhileNode) new WhileNode(expression, body).setLineNumber(tokens.get(startIndex).lineNum());
    }

    private IfNode matchIfStatement() throws Exception {
        final int startIndex = current;

        if (!match(KEYWORD, LPAREN) || !"if".equals(peek().value())) {
            throw new Exception("Error parsing - couldn't match 'if' statement " + getLineNumberStatement(setCurrentToken(startIndex)));
        }
        consume(2);

        ExpressionNode expression;
        try {
            expression = matchExpression();
        } catch (Exception e) {
            setCurrentToken(startIndex);
            throw e;
        }

        if (!match(RPAREN)) {
            throw new Exception("Error parsing - couldn't match 'while' statement, missing right parenthesis " + getLineNumberStatement(setCurrentToken(startIndex)));
        }
        consume(); // ignore right parenthesis

        CompoundNode body;
        try {
            body = matchCompoundStatement();
        } catch (Exception e) {
            setCurrentToken(startIndex);
            throw e;
        }

        return (IfNode) new IfNode(expression, body).setLineNumber(tokens.get(startIndex).lineNum());
    }

    private CompoundNode matchCompoundStatement() throws Exception {
        final int startIndex = current;

        if (!match(LCURLY)) {
            throw new Exception("Error parsing - couldn't match 'compound' statement, missing left curly bracket " + getLineNumberStatement(setCurrentToken(startIndex)));
        }
        consume();

        List<StatementNode> nestedStatements = new ArrayList<>();
        StatementNode statement = null;
        while (!match(RCURLY)) {
            try {
                statement = matchStatement();
                nestedStatements.add(statement);
            } catch (Exception e) {
                setCurrentToken(startIndex);
                throw e;
            }
        }
        consume();

        return (CompoundNode) new CompoundNode(nestedStatements).setLineNumber(tokens.get(startIndex).lineNum());
    }

    private ExpressionNode matchExpression() throws Exception {
        try {
            return matchBinExpr();
        } catch (Exception _) {
        }
        try {
            return matchFunctionCallExpression();
        } catch (Exception _) {
        }
        try {
            return matchUnaryExpression();
        } catch (Exception _) {
        }
        try {
            return matchIndexingExpr();
        } catch (Exception _) {
        }
        try {
            return matchLiteralExpression();
        } catch (Exception _) {
        }
        throw new Exception("Error parsing - couldn't match expression " + getLineNumberStatement(current));
    }


    private ExpressionNode matchExpressionForBinExpr() throws Exception {
        try {
            return matchFunctionCallExpression();
        } catch (Exception _) {
        }
        try {
            return matchUnaryExpression();
        } catch (Exception _) {
        }
        try {
            return matchIndexingExpr();
        } catch (Exception _) {
        }
        try {
            return matchLiteralExpression();
        } catch (Exception _) {
        }
        throw new Exception("Error parsing - couldn't match expression " + getLineNumberStatement(current));
    }

    private ExpressionNode matchUnaryExpression() throws Exception {
        final int startIndex = current;

        if (!(match(MINUS) || match(NOT)))
            throw new Exception("Error parsing - no unary operator found" + getLineNumberStatement(setCurrentToken(startIndex)));

        try {
            return (UnaryExprNode) new UnaryExprNode(consume().type(), matchLiteralExpression()).setLineNumber(tokens.get(startIndex).lineNum());
        } catch (Exception e) {
            setCurrentToken(startIndex);
            throw e;
        }
    }

    private BinExprNode matchBinExpr() throws Exception {
        final int startIndex = current;

        ExpressionNode leftExpr = null;
        try {
            leftExpr = matchExpressionForBinExpr();
        } catch (Exception e) {
            setCurrentToken(startIndex);
            throw e;
        }

        if (!(peek() != null && peek().type().isBinOperator())) {
            throw new Exception("Error parsing - expression operator, is not a binary operator" + getLineNumberStatement(setCurrentToken(startIndex)));
        }
        TokenType operator = consume().type();

        ExpressionNode rightExpr = null;
        try {
            rightExpr = matchExpression();
        } catch (Exception e) {
            setCurrentToken(startIndex);
            throw e;
        }

        return (BinExprNode) new BinExprNode(operator, leftExpr, rightExpr).setLineNumber(tokens.get(startIndex).lineNum());
    }

    private IndexingExprNode matchIndexingExpr() throws Exception {
        final int startIndex = current;

        ExpressionNode leftExpr = null;
        try {
            leftExpr = matchLiteralExpression();
        } catch (Exception e) {
            setCurrentToken(startIndex);
            throw e;
        }

        if (!match(LSQUARE))
            throw new Exception("Error parsing - left square bracket not found for indexing expression" + getLineNumberStatement(setCurrentToken(startIndex)));
        consume();

        ExpressionNode indexExpr = null;
        try {
            indexExpr = matchExpression();
        } catch (Exception e) {
            setCurrentToken(startIndex);
            throw e;
        }

        if (!match(RSQUARE))
            throw new Exception("Error parsing - left square bracket not found for indexing expression" + getLineNumberStatement(setCurrentToken(startIndex)));
        consume();

        return (IndexingExprNode) new IndexingExprNode(leftExpr, indexExpr).setLineNumber(tokens.get(startIndex).lineNum());
    }

    private FunctionCallNode matchFunctionCallExpression() throws Exception {
        final int startIndex = current;

        if (!match(IDENTIFIER, LPAREN))
            throw new Exception("Error parsing - couldn't match function call expression" + getLineNumberStatement(current));
        String functionName = consume().value();
        consume();

        List<ExpressionNode> args = new ArrayList<>();
        try {
            args.add(matchExpression());
        } catch (Exception e) {
            if (match(RPAREN)) {
                consume();
                return (FunctionCallNode) new FunctionCallNode(functionName, args).setLineNumber(tokens.get(startIndex).lineNum());
            }
        }

        try {
            while (match(COMMA)) {
                consume();
                args.add(matchExpression());
            }
        } catch (Exception e) {
            setCurrentToken(startIndex);
            throw e;
        }
        if (match(RPAREN)) {
            consume();
            return (FunctionCallNode) new FunctionCallNode(functionName, args).setLineNumber(tokens.get(startIndex).lineNum());
        }
        throw new Exception("Error parsing - couldn't match function call expression, no closing parenthesis" + getLineNumberStatement(setCurrentToken(startIndex)));
    }

    private ExpressionNode matchLiteralExpression() throws Exception {
        final int startIndex = current;

        if (peek() == null)
            throw new Exception("Error parsing - couldn't match literal expression, no tokens left " + getLineNumberStatement(current));
        if (match(INTCONST)) {
            return (IntegerLiteralNode) new IntegerLiteralNode(Integer.valueOf(consume().value())).setLineNumber(tokens.get(startIndex).lineNum());
        } else if (match(STRCONST)) {
            return (StringLiteralNode) new StringLiteralNode(consume().value()).setLineNumber(tokens.get(startIndex).lineNum());
        } else if (match(IDENTIFIER)) {
            return (IdentifierNode) new IdentifierNode(consume().value()).setLineNumber(tokens.get(startIndex).lineNum());
        } else if (match(LPAREN)) {
            consume(); // ignore left parenthesis
            ExpressionNode nestedExpr = matchExpression();
            if (match(RPAREN)) {
                consume();
                return (ExpressionNode) nestedExpr.setLineNumber(tokens.get(startIndex).lineNum());
            }
            throw new Exception("Error parsing - couldn't match literal expression, missing right parenthesis " + getLineNumberStatement(setCurrentToken(startIndex)));
        } else {
            throw new Exception("Error parsing - couldn't match literal expression " + getLineNumberStatement(current));
        }
    }

    private boolean match(TokenType... tokentypes) {
        for (int i = 0; i < tokentypes.length; i++) {
            if (!(peek(i) == null ? false : peek(i).type() == tokentypes[i])) {
                return false;
            }
        }
        return true;
    }

    // returns current token without incrementing, null if no tokens left
    private Token peek() {
        try {
            return tokens.get(current);
        } catch (Exception _) {
            return null;
        }
    }

    // returns token list without incrementing, null if no tokens left
    private Token peek(int ahead) {
        try {
            return tokens.get(current + ahead);
        } catch (Exception _) {
            return null;
        }
    }

    // returns current token and increments, null if no tokens left
    private Token consume() {
        try {
            return tokens.get(current++);
        } catch (Exception _) {
            return null;
        }
    }

    // returns a list and increments, items in list may be null if no tokens left
    private List<Token> consume(int times) {
        List<Token> tokens = new ArrayList<>();
        for (int i = 0; i < times; i++) {
            tokens.add(consume());
        }
        return tokens;

    }

    private String getLineNumberStatement(int tokenNum) {
        return "[Line: %s]".formatted(tokens.get(tokenNum) == null ? tokens.getLast().lineNum() : tokens.get(tokenNum).lineNum());
    }

    private int setCurrentToken(int newIndex) {
        int old = this.current;
        current = newIndex;
        return old;
    }
}
