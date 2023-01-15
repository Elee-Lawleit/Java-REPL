package repl;

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

public abstract class SyntaxNode {
    public abstract SyntaxKind getKind();

    public abstract Object getValue();

    public abstract Iterable<SyntaxNode> getChildren();
}