package repl;

import java.util.ArrayList;

public class NumberExpressionSyntax extends ExpressionSyntax {

    private SyntaxToken numberToken;

    // constructor
    public NumberExpressionSyntax(SyntaxToken numberToken) {
        this.numberToken = numberToken;
    }

    @Override
    public SyntaxKind getKind() {
        return SyntaxKind.NumberExpression;
    }

    // getter
    public SyntaxToken getNumberToken() {
        return numberToken;
    }

    @Override
    public Iterable<SyntaxNode> getChildren() {

        // this works because the ArrayList implements the Iterable interface

        ArrayList<SyntaxNode> arr = new ArrayList<SyntaxNode>();
        arr.add(numberToken);

        return arr;
    }

    @Override
    public Object getValue() {
        return numberToken.value;
    }
}