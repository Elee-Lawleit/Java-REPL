package repl;

public enum SyntaxKind {
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
    BinaryExpression,
    ParenthesizedExpression
}
