package repl;

import java.util.ArrayList;

import repl.SyntaxToken;
import repl.ExpressionSyntax;

public final class ParenthesizedExpression extends ExpressionSyntax{

    public ParenthesizedExpression(SyntaxToken openParenthesisToken, ExpressionSyntax expression, SyntaxToken closeParenthesis){
        this.openParenthesisToken = openParenthesisToken;
        this.expression = expression;
        this.closeParenthesisToken = closeParenthesis;
    }

    private SyntaxToken openParenthesisToken;
    private ExpressionSyntax expression;
    private SyntaxToken closeParenthesisToken;

    //getters
    public SyntaxToken getOpenParenthesisToken(){
        return openParenthesisToken;
    }
    public SyntaxToken getCloseParenthesisToken(){
        return closeParenthesisToken;
    }
    public ExpressionSyntax getExpression(){
        return expression;
    }

    @Override
    public SyntaxKind getKind() {
        return SyntaxKind.ParenthesizedExpression;
    }

    @Override
    public Object getValue() {
        //not going to be used so
        return null;
    }

    @Override
    public Iterable<SyntaxNode> getChildren() {
        ArrayList<SyntaxNode> AllFields = new ArrayList<SyntaxNode>();
        AllFields.add(openParenthesisToken);
        AllFields.add(expression);
        AllFields.add(closeParenthesisToken);

        return AllFields;
    }

}