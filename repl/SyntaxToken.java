package repl;

import java.util.ArrayList;
import repl.SyntaxKind;

public class SyntaxToken extends SyntaxNode {
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