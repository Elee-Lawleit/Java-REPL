import java.util.ArrayList;
import java.util.Scanner;

import org.omg.CORBA._IDLTypeStub;

import java.util.Collection;
import java.beans.Expression;
import java.lang.Character;
import java.lang.Integer;
import java.lang.String;



class Main {
    public static void main(String[] args) throws Exception {
        
        Scanner input = new Scanner(System.in);

        boolean showTree = false;
        while (true) {
            System.out.print("> ");

            String line = input.nextLine();

            if (line == null || line.isEmpty() || line.trim().isEmpty()) {
                return;
            }

            if(line.equals("#showTree")){
                showTree = !showTree;
                System.out.print(ConsoleColors.RED);

                System.out.println(showTree? "Showing Parse Trees." : "Not Showing Parse Trees.");

                System.out.print(ConsoleColors.RESET);
                continue;
            }
            else if(line.equals("#cls")){
                System.out.print("\033[H\033[2J");
                continue;
            }

            SyntaxTree syntaxTree = SyntaxTree.parse(line);

            if(showTree){
                prettyPrint(syntaxTree.root, "", true);
            }

            //printing errors
            Parser parser = new Parser(line);
            if(!syntaxTree.diagnostics.isEmpty()){
                ArrayList<String> errors = parser.getDiagnostics();

                for(String error: errors){
                    System.out.print(ConsoleColors.RED); //--> change console color to red
                    System.out.println(error);
                    System.out.print(ConsoleColors.RESET);
                }
            }
            else{
                Evaluator evaluator = new Evaluator(syntaxTree.root);

                int result = evaluator.evaluate();

                System.out.println(result);
            }

            //DISPLAYING LEXER OUTPUT
            System.out.println();
            System.out.println();
            Lexer lexer = new Lexer(line);
            while (true) {
                SyntaxToken token = lexer.nextToken();

                if (token.kind == SyntaxKind.EndOfFileToken) {
                    break;
                }

                System.out.print("{ TokenKind: " + token.kind + ", TokenText: " + token.text);

                if (token.value != null) {
                    System.out.println(" TokenValue: " + token.value + " }");
                } else {
                    System.out.println(" }");
                }
            }
        }
    }

    // to print the abstract syntax tree
    static void prettyPrint(SyntaxNode node, String indent, boolean isLast) {
        
        String marker = isLast ? "└──" : "├──";

        System.out.print(indent);
        System.out.print(marker);
        System.out.print(node.getKind());

        if (node.getValue() != null) {
            // System.out.print(" [ASDASD: " + node.getClass().getName() + "] ");
            System.out.print(" ");
            System.out.print(node.getValue());
        }

        System.out.println();

        // indent += "    ";

        //purely for formatting
        indent+= isLast? "    " : "│    ";


        //      +
        //     / \
        //    1   +
        //       / \
        //      2   3
        

        while (true) {
            // Object child = (Object) node.getChildren();
            Iterable<SyntaxNode> child =  node.getChildren();

            // System.out.println("CHILD: " + child);

            if (child.getClass().getName().equals("java.util.ArrayList")) {
                ArrayList chil = (ArrayList) child;
                if (chil.isEmpty()) {
                    // System.out.println("Is this even working?");
                    break;
                }
          
                // if(!child.iterator().hasNext()){
                //     last = true;
                // }

                for(int i = 0; i < ((ArrayList<?>) child).size(); i++ ){
                    ArrayList<SyntaxNode> obj = (ArrayList) child;

                    prettyPrint(obj.get(i), indent, i == ((Collection<?>) child).size() - 1);
                }


                // int count = 0;
                // for (SyntaxNode arrChild :  child) {
                //     prettyPrint(arrChild, indent, last);
                //     count++;
                // }
                // System.out.println("COUNT: " + count);
            } else {
                break;
            }
            break;
        }

    }
}

// Kinds of tokens accepted in our language
enum SyntaxKind {
    NumberToken,
    WhiteSpaceToken,
    PlusToken,
    MinusToken,
    StarToken,
    SlashToken,
    OpenParenthesisToken,
    CloseParenthesisToken,
    BadToken,
    EndOfFileToken,
    // parser constants from here
    NumberExpression, 
    BinaryExpression, ParenthesizedExpression
}

class SyntaxToken extends SyntaxNode {
    // needed variables
    public SyntaxKind kind;
    public int position;
    public String text;
    public Object value;

    // Constructor to initialize the token
    public SyntaxToken(SyntaxKind kind, int position, String text, Object value) {
        this.kind = kind;
        this.position = position;
        this.text = text;
        this.value = value;
    }

    // getters
    public SyntaxKind getKind() {
        return this.kind;
    }

    public int getPosition() {
        return this.position;
    }

    public String text() {
        return this.text;
    }

    // just putting this here for now
    @Override
    public Iterable<SyntaxNode> getChildren() {
        // System.out.println("CODE REACHING HERE");
        ArrayList<SyntaxNode> a1 = new ArrayList<>();
        return a1;
    }

    @Override
    public Object getValue() {
        return value;
    }

}

class Lexer {

    private String text;
    private int position;
    private ArrayList<String> _diagnostics = new ArrayList<String>();

    // to initialize the text
    public Lexer(String text) {
        this.text = text;
    }

    //return the errors
    public ArrayList<String> getDiagnostics(){
        return _diagnostics;
    }

    // get current character
    public char getCurrentCharacter() {
        if (position >= text.length()) {
            return '\0';
        }

        return text.charAt(position);
    }

    // helper method... increments position
    private void next() {
        position++;
    }

    // returns next word i-e., token
    public SyntaxToken nextToken() {
        // <numbers>
        // + = * / ( )
        // <whitesapce>
        // EOF (end of file)

        if (position >= text.length()) {
            return new SyntaxToken(SyntaxKind.EndOfFileToken, position, "\0", null);
        }

        char currentCharacter = getCurrentCharacter();

        // checking for digits
        if (Character.isDigit(currentCharacter)) {
            int start = position;

            while (Character.isDigit(getCurrentCharacter())) {
                next();
            }

            int length = position - start;

            // to make the substring method behave like the C# version of substring method
            // --> substring(startIndex, length)

            String text = this.text.substring(start, Math.min(start + length, this.text.length()));

            Integer value = null;
            try{
                value = Integer.parseInt(text);
            }
            catch(NumberFormatException ex){
                _diagnostics.add("The number " + text + " isn't valid int.");
            }

            return new SyntaxToken(SyntaxKind.NumberToken, start, text, value);
        }

        // to check for white spaces
        if (Character.isWhitespace(currentCharacter)) {
            int start = position;

            while (Character.isWhitespace(getCurrentCharacter())) {
                next();
            }

            return new SyntaxToken(SyntaxKind.WhiteSpaceToken, start, " ", null);
        }

        // operators
        if (currentCharacter == '+') {
            return new SyntaxToken(SyntaxKind.PlusToken, position++, "+", null);
        }

        else if (currentCharacter == '-') {
            return new SyntaxToken(SyntaxKind.MinusToken, position++, "-", null);
        } else if (currentCharacter == '*') {
            return new SyntaxToken(SyntaxKind.StarToken, position++, "*", null);
        } else if (currentCharacter == '/') {
            return new SyntaxToken(SyntaxKind.SlashToken, position++, "/", null);
        } else if (currentCharacter == '(') {
            return new SyntaxToken(SyntaxKind.OpenParenthesisToken, position++, "(", null);
        } else if (currentCharacter == ')') {
            return new SyntaxToken(SyntaxKind.CloseParenthesisToken, position++, ")", null);
        }

        // if we get to this point, then it's a token not supported by our language

        _diagnostics.add("ERROR: bad character input: " + getCurrentCharacter());
        return new SyntaxToken(SyntaxKind.BadToken, position++, this.text.substring(position - 1, position), null);
    }
}

// when making the syntax tree

// 1 + 2 * 3

// +
// / \
// 1 *
// / \
// 2 3

// 1 is a node here, let's say a Number Node
// + is also a node here, let's say a BinaryOperatorNode
// ->binary because it works on 2 operands
// and so on, you get the gist of it

abstract class SyntaxNode {
    public abstract SyntaxKind getKind();

    public abstract Object getValue();

    public abstract Iterable<SyntaxNode> getChildren();
}

abstract class ExpressionSyntax extends SyntaxNode {

}

final class NumberExpressionSyntax extends ExpressionSyntax {

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

final class BinaryExpressionSyntax extends ExpressionSyntax {
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

final class ParenthesizedExpression extends ExpressionSyntax{

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

final class SyntaxTree{
    public SyntaxTree(ArrayList<String> diagnostics, ExpressionSyntax root, SyntaxToken endOfFile){
        this.diagnostics = diagnostics;
        this.root = root;
        this.endOfFile = endOfFile;

    }

    ArrayList<String> diagnostics;
    ExpressionSyntax root;
    SyntaxToken endOfFile;

    //getters
    ArrayList<String> getDiagnostics(){
        return diagnostics;
    }
    ExpressionSyntax getRoot(){
        return root;
    }
    SyntaxToken getEndOfFile(){
        return endOfFile;
    }

    public static SyntaxTree parse(String text){
        Parser parser = new Parser(text);
        return parser.parse();
    }
}

class Parser {

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

class Evaluator{

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





//just for console colors
class ConsoleColors {
    // Reset
    public static final String RESET = "\033[0m"; // Text Reset

    // Regular Colors
    public static final String BLACK = "\033[0;30m"; // BLACK
    public static final String RED = "\033[0;31m"; // RED
    public static final String GREEN = "\033[0;32m"; // GREEN
    public static final String YELLOW = "\033[0;33m"; // YELLOW
    public static final String BLUE = "\033[0;34m"; // BLUE
    public static final String PURPLE = "\033[0;35m"; // PURPLE
    public static final String CYAN = "\033[0;36m"; // CYAN
    public static final String WHITE = "\033[0;37m"; // WHITE

    // Bold
    public static final String BLACK_BOLD = "\033[1;30m"; // BLACK
    public static final String RED_BOLD = "\033[1;31m"; // RED
    public static final String GREEN_BOLD = "\033[1;32m"; // GREEN
    public static final String YELLOW_BOLD = "\033[1;33m"; // YELLOW
    public static final String BLUE_BOLD = "\033[1;34m"; // BLUE
    public static final String PURPLE_BOLD = "\033[1;35m"; // PURPLE
    public static final String CYAN_BOLD = "\033[1;36m"; // CYAN
    public static final String WHITE_BOLD = "\033[1;37m"; // WHITE

    // Underline
    public static final String BLACK_UNDERLINED = "\033[4;30m"; // BLACK
    public static final String RED_UNDERLINED = "\033[4;31m"; // RED
    public static final String GREEN_UNDERLINED = "\033[4;32m"; // GREEN
    public static final String YELLOW_UNDERLINED = "\033[4;33m"; // YELLOW
    public static final String BLUE_UNDERLINED = "\033[4;34m"; // BLUE
    public static final String PURPLE_UNDERLINED = "\033[4;35m"; // PURPLE
    public static final String CYAN_UNDERLINED = "\033[4;36m"; // CYAN
    public static final String WHITE_UNDERLINED = "\033[4;37m"; // WHITE

    // Background
    public static final String BLACK_BACKGROUND = "\033[40m"; // BLACK
    public static final String RED_BACKGROUND = "\033[41m"; // RED
    public static final String GREEN_BACKGROUND = "\033[42m"; // GREEN
    public static final String YELLOW_BACKGROUND = "\033[43m"; // YELLOW
    public static final String BLUE_BACKGROUND = "\033[44m"; // BLUE
    public static final String PURPLE_BACKGROUND = "\033[45m"; // PURPLE
    public static final String CYAN_BACKGROUND = "\033[46m"; // CYAN
    public static final String WHITE_BACKGROUND = "\033[47m"; // WHITE

    // High Intensity
    public static final String BLACK_BRIGHT = "\033[0;90m"; // BLACK
    public static final String RED_BRIGHT = "\033[0;91m"; // RED
    public static final String GREEN_BRIGHT = "\033[0;92m"; // GREEN
    public static final String YELLOW_BRIGHT = "\033[0;93m"; // YELLOW
    public static final String BLUE_BRIGHT = "\033[0;94m"; // BLUE
    public static final String PURPLE_BRIGHT = "\033[0;95m"; // PURPLE
    public static final String CYAN_BRIGHT = "\033[0;96m"; // CYAN
    public static final String WHITE_BRIGHT = "\033[0;97m"; // WHITE

    // Bold High Intensity
    public static final String BLACK_BOLD_BRIGHT = "\033[1;90m"; // BLACK
    public static final String RED_BOLD_BRIGHT = "\033[1;91m"; // RED
    public static final String GREEN_BOLD_BRIGHT = "\033[1;92m"; // GREEN
    public static final String YELLOW_BOLD_BRIGHT = "\033[1;93m";// YELLOW
    public static final String BLUE_BOLD_BRIGHT = "\033[1;94m"; // BLUE
    public static final String PURPLE_BOLD_BRIGHT = "\033[1;95m";// PURPLE
    public static final String CYAN_BOLD_BRIGHT = "\033[1;96m"; // CYAN
    public static final String WHITE_BOLD_BRIGHT = "\033[1;97m"; // WHITE

    // High Intensity backgrounds
    public static final String BLACK_BACKGROUND_BRIGHT = "\033[0;100m";// BLACK
    public static final String RED_BACKGROUND_BRIGHT = "\033[0;101m";// RED
    public static final String GREEN_BACKGROUND_BRIGHT = "\033[0;102m";// GREEN
    public static final String YELLOW_BACKGROUND_BRIGHT = "\033[0;103m";// YELLOW
    public static final String BLUE_BACKGROUND_BRIGHT = "\033[0;104m";// BLUE
    public static final String PURPLE_BACKGROUND_BRIGHT = "\033[0;105m"; // PURPLE
    public static final String CYAN_BACKGROUND_BRIGHT = "\033[0;106m"; // CYAN
    public static final String WHITE_BACKGROUND_BRIGHT = "\033[0;107m"; // WHITE
}