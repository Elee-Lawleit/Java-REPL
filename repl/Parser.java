package repl;

import java.util.ArrayList;


public class Parser {

    // fields
    private SyntaxToken[] tokens;
    private int position; // --> current token
    private ArrayList<String> _diagnostics = new ArrayList<String>();

    public Parser(String text) {

        ArrayList<SyntaxToken> tokensLexed = new ArrayList<>();

        Lexer lexer = new Lexer(text);
        SyntaxToken token;

        do {
            token = lexer.nextToken();

            if (token.kind != SyntaxKind.WhiteSpaceToken &&
                    token.kind != SyntaxKind.BadToken) {
                tokensLexed.add(token);
            }

        } while (token.kind != SyntaxKind.EndOfFileToken);

        // typecast it to SyntaxToken array instead of Object[]

        tokens = new SyntaxToken[tokensLexed.size()];

        for (int i = 0; i < tokensLexed.size(); i++) {
            tokens[i] = tokensLexed.get(i);
        }

        //to see which errors were reported by Lexer
        _diagnostics.addAll(lexer.getDiagnostics());

    }

    // return the errors
    public ArrayList<String> getDiagnostics() {
        return _diagnostics;
    }

    private SyntaxToken lookAhead(int offset) {
        int index = position + offset;

        if (index >= tokens.length) {
            // return the last token,
            // which would be EOF, right? I think so
            return tokens[tokens.length - 1];
        }

        return tokens[index];
    }

    // you might this is the same as GTACP() but
    // it also increments the position by 1, so

    // also, there might be a better way to do this
    private SyntaxToken nextToken() {
        SyntaxToken current = getTokenAtCurrentPosition();
        position++;
        return current;
    }

    private SyntaxToken getTokenAtCurrentPosition() {
        // just return whatever is at the current position
        return lookAhead(0);
    }

    private SyntaxToken matchToken(SyntaxKind kind) {
        if (getTokenAtCurrentPosition().kind == kind) {
            return nextToken();
        }
        _diagnostics.add("ERROR: Unexpected token <" + getTokenAtCurrentPosition() +">, expected <" + kind + ">");
        return new SyntaxToken(kind, getTokenAtCurrentPosition().position, null, null);
    }

    public SyntaxTree parse(){
        ExpressionSyntax expression = parseTerm();
        SyntaxToken endOfFileToken = matchToken(SyntaxKind.EndOfFileToken);
        return new SyntaxTree(getDiagnostics(), expression, endOfFileToken);
    }

    // only calls the parseTerm() method 
    private ExpressionSyntax parseExpression(){
        return parseTerm();
    }

    //to parse trees/subtrees of lower priority

    // to parse the left side basically
    public ExpressionSyntax parseTerm() {

        // get the exp to the left of operator
        ExpressionSyntax left = parseFactor(); //--> for 1 + 2 + 3, left = 1

        //left means right ka left, you know

        // PLUS and MINUS currently in this language have the least and same precedence

        // the evaluation will start from the least precedent operator to make the
        // syntax tree

        // so the order of the functions is going to be in that order as well

        while (getTokenAtCurrentPosition().kind == SyntaxKind.PlusToken
                || getTokenAtCurrentPosition().kind == SyntaxKind.MinusToken
                ) {

            // get the operator at current position
            SyntaxToken operatorToken = nextToken(); //--> for 1 + 2 + 3, operatorToken = +

            // get the exp to the right of operator
            ExpressionSyntax right = parseFactor(); //--> for 1 + 2 + 3, right = 2

            // making a binary expression syntax
            left = new BinaryExpressionSyntax(left, operatorToken, right);  // in very iteration, the params will be  (1 + 2) 
        }

        // return the binary expression
        return left;
    }

    // to parse trees/sub-trees with higher priority

    // to parse the right side
    public ExpressionSyntax parseFactor() {

        // get the exp to the left of operator
        ExpressionSyntax left = parsePrimaryExpression(); //--> for 1 + 2 + 3, left = 1

        // PLUS and MINUS currently in this language have the least and same precedence

        // the evaluation will start from the least precedent operator to make the
        // syntax tree

        // so the order of the functions is going to be in that order as well

        while ( getTokenAtCurrentPosition().kind == SyntaxKind.StarToken
                || getTokenAtCurrentPosition().kind == SyntaxKind.SlashToken) {

            // get the operator at current position
            SyntaxToken operatorToken = nextToken(); //--> for 1 + 2 + 3, operatorToken = +

            // get the exp to the right of operator
            ExpressionSyntax right = parsePrimaryExpression(); //--> for 1 + 2 + 3, right = 2

            // making a binary expression syntax
            left = new BinaryExpressionSyntax(left, operatorToken, right);  // in very iteration, the params will be  (1 + 2) 
        }

        // return the binary expression
        return left;
    }

    private ExpressionSyntax parsePrimaryExpression() {
        // if it matches to be Number, for now

        if(getTokenAtCurrentPosition().kind == SyntaxKind.OpenParenthesisToken){

            //get the left bracket
            SyntaxToken left = nextToken();

            //get the parsed expression
            ExpressionSyntax expression = parseExpression();

            //get the right bracket
            SyntaxToken right = matchToken(SyntaxKind.CloseParenthesisToken);

            return new ParenthesizedExpression(left, expression, right);
        }

        SyntaxToken numberToken = matchToken(SyntaxKind.NumberToken);

        return new NumberExpressionSyntax(numberToken);
    }
}