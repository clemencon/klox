package dev.clemencon.klox.scanner

/**
 * All token types in the Lox language.
 * Each token produced by the scanner is classified into one of these types,
 * allowing the parser to quickly identify the role of each lexeme without string comparisons.
 * The types are organized into logical groups for clarity.
 */
enum class TokenType {
    // Single-character tokens (delimiters and single-character operators).
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
    COMMA, DOT, MINUS, PLUS, SEMICOLON, SLASH, STAR,

    // One or two character operators (require lookahead during scanning).
    // Example: '!' can be BANG or '!=' can be BANG_EQUAL.
    BANG, BANG_EQUAL,
    EQUAL, EQUAL_EQUAL,
    GREATER, GREATER_EQUAL,
    LESS, LESS_EQUAL,

    // Literals (values that need runtime representation).
    IDENTIFIER, STRING, NUMBER,

    // Reserved keywords (identified during scanning by checking against keyword map).
    AND, CLASS, ELSE, FALSE, FUN, FOR, IF, NIL, OR,
    PRINT, RETURN, SUPER, THIS, TRUE, VAR, WHILE,

    // End of file marker (signals to the parser that input is exhausted).
    EOF
}