package repl;

import repl.ConsoleColors;
import java.util.ArrayList;

public final class BinaryExpressionSyntax extends ExpressionSyntax {
    // 1 + 2 * 3 - 4

    // left is the expression to the left of the op
    // 1 + 2 to the left of *

    // right is the exp to the right of the operator
    // 3 - 4 to the right of *

    private ExpressionSyntax left;
    private SyntaxToken operatorToken;
    private ExpressionSyntax right;

    public BinaryExpressionSyntax(ExpressionSyntax left, SyntaxToken operatorToken, ExpressionSyntax right) {
        this.left = left;
        this.operatorToken = operatorToken;
        this.right = right;
    }

    @Override
    public SyntaxKind getKind() {
        return SyntaxKind.BinaryExpression;
    }

    // getters
    public ExpressionSyntax getLeftExpression() {
        return left;
    }

    public ExpressionSyntax getRightExpression() {
        return right;
    }

    public SyntaxToken getBinaryOperator() {
        return operatorToken;
    }

    @Override
    public Iterable<SyntaxNode> getChildren() {

        // not doing this since its cumborsome, also, java doesn't support multi return
        // values
        // so An array is needed here anyway

        // ArrayList<SyntaxNode> _left = new ArrayList<SyntaxNode>();
        // _left.add(left);

        // ArrayList<SyntaxNode> _operatorToken = new ArrayList<SyntaxNode>();
        // _operatorToken.add(operatorToken);

        // ArrayList<SyntaxNode> _right = new ArrayList<SyntaxNode>();
        // _left.add(right);

        // SyntaxNode works here because ExpressionSyntax extends SyntaxNode,
        // so ExpressionSyntax objects can be assigned to SyntaxNode class

        ArrayList<SyntaxNode> AllFields = new ArrayList<SyntaxNode>();
        AllFields.add(left);
        AllFields.add(operatorToken);
        AllFields.add(right);

        return AllFields;
    }

    @Override
    public Object getValue() {
        ArrayList<SyntaxNode> AllFields = new ArrayList<SyntaxNode>();
        AllFields.add(left);
        AllFields.add(operatorToken);
        AllFields.add(right);

        return AllFields;
    }
}