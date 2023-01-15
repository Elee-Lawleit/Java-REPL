package repl;

import java.util.ArrayList;


public final class SyntaxTree{
    public SyntaxTree(ArrayList<String> diagnostics, ExpressionSyntax root, SyntaxToken endOfFile){
        this.diagnostics = diagnostics;
        this.root = root;
        this.endOfFile = endOfFile;

    }

    ArrayList<String> diagnostics;
    ExpressionSyntax root;
    SyntaxToken endOfFile;

    //getters
    public ArrayList<String> getDiagnostics(){
        return diagnostics;
    }
    public ExpressionSyntax getRoot(){
        return root;
    }
    public SyntaxToken getEndOfFile(){
        return endOfFile;
    }

    public static SyntaxTree parse(String text){
        Parser parser = new Parser(text);
        return parser.parse();
    }
}