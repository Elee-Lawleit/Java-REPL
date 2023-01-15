package repl;

import java.util.ArrayList;

public class Lexer {

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