import java.util.ArrayList;
import java.util.Scanner;

import java.util.Collection;
import java.beans.Expression;
import java.lang.Character;
import java.lang.Integer;
import java.lang.String;

import repl.ConsoleColors;
import repl.Parser;
import repl.SyntaxKind;
import repl.SyntaxTree;
import repl.Evaluator;
import repl.Lexer;
import repl.SyntaxToken;
import repl.SyntaxNode;



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
                prettyPrint(syntaxTree.getRoot(), "", true);
            }

            //printing errors
            Parser parser = new Parser(line);
            if(!syntaxTree.getDiagnostics().isEmpty()){
                ArrayList<String> errors = parser.getDiagnostics();

                for(String error: errors){
                    System.out.print(ConsoleColors.RED); //--> change console color to red
                    System.out.println(error);
                    System.out.print(ConsoleColors.RESET);
                }
            }
            else{
                Evaluator evaluator = new Evaluator(syntaxTree.getRoot());

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
