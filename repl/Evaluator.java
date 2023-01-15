package repl;

public class Evaluator{

    private ExpressionSyntax root;

    public Evaluator(ExpressionSyntax root){
        this.root = root;
    }

    public int evaluate() throws Exception{
        return evaluateExpression(root);
    }

    private int evaluateExpression(ExpressionSyntax root) throws Exception {
        if(root instanceof NumberExpressionSyntax){
            NumberExpressionSyntax n = (NumberExpressionSyntax) root;

            return (int) n.getNumberToken().value;
        }

        if(root instanceof BinaryExpressionSyntax){
            BinaryExpressionSyntax b = (BinaryExpressionSyntax) root;

            int left = evaluateExpression(b.getLeftExpression());
            int right = evaluateExpression(b.getRightExpression());

            if(b.getBinaryOperator().kind == SyntaxKind.PlusToken){
                return left + right;
            }
            else if (b.getBinaryOperator().kind == SyntaxKind.MinusToken) {
                return left- right;
            }
            else if (b.getBinaryOperator().kind == SyntaxKind.StarToken) {
                return left * right;
            }
            else if (b.getBinaryOperator().kind == SyntaxKind.SlashToken) {
                return left / right;
            }
            else{
                throw new Exception("Unexpected binary operator " + b.getBinaryOperator().kind);
            }
        }

        if(root instanceof ParenthesizedExpression){
            ParenthesizedExpression p = (ParenthesizedExpression) root;
            return evaluateExpression(p.getExpression());
        }

        throw new Exception("Unexpected node " + root.getKind());
    }
}