package dev.clemencon.klox.scanner

import dev.clemencon.klox.Lox
import dev.clemencon.klox.scanner.TokenType.*

/**
 * Lexical scanner for Lox source code.
 * Converts raw source code into tokens using two position markers:
 * - startPosition: where the current lexeme begins
 * - currentPosition: the character being examined
 */
class Scanner(private val sourceCode: String) {
    private val tokens = mutableListOf<Token>()

    private var startPosition = 0
    private var currentPosition = 0
    private var lineNumber = 1

    /**
     * Scans the entire source code and returns tokens including a final EOF.
     * Processes one lexeme per iteration, marking the start position before each scan operation.
     */
    fun scanTokens(): List<Token> {
        while (!isAtEnd()) {
            startPosition = currentPosition
            scanToken()
        }

        tokens.add(Token(type = EOF, lexeme = "", literal = null, lineNumber))
        return tokens
    }

    /**
     * Recognizes and emits a single token.
     * Multi-character operators use lookahead via match().
     * Complex literals (strings, numbers, identifiers) delegate to specialized methods.
     */
    private fun scanToken() {
        val character = advance()
        when {
            character == '(' -> addToken(LEFT_PAREN)
            character == ')' -> addToken(RIGHT_PAREN)
            character == '{' -> addToken(LEFT_BRACE)
            character == '}' -> addToken(RIGHT_BRACE)
            character == ',' -> addToken(COMMA)
            character == '.' -> addToken(DOT)
            character == '-' -> addToken(MINUS)
            character == '+' -> addToken(PLUS)
            character == ';' -> addToken(SEMICOLON)
            character == '*' -> addToken(STAR)

            character == '!' -> addToken(if (match('=')) BANG_EQUAL else BANG)
            character == '=' -> addToken(if (match('=')) EQUAL_EQUAL else EQUAL)
            character == '<' -> addToken(if (match('=')) LESS_EQUAL else LESS)
            character == '>' -> addToken(if (match('=')) GREATER_EQUAL else GREATER)

            character == '/' -> if (match('/')) {
                while (peek() != '\n' && !isAtEnd()) advance()
            } else {
                addToken(SLASH)
            }

            character == '\n' -> lineNumber++
            character.isWhitespace() -> {}

            character == '"' -> string()
            character.isDigit() -> number()
            character.isLetter() || character == '_' -> identifier()

            else -> Lox.error(lineNumber, "Unexpected character.")
        }
    }

    /**
     * Scans a number literal (integer or decimal).
     * Uses two-character lookahead to consume '.' only when followed by a digit.
     * Prevents "123.toString()" from being lexed as "123." + "toString()".
     */
    private fun number() {
        while (peek().isDigit()) advance()

        if (peek() == '.' && peekNext().isDigit()) {
            advance()
            while (peek().isDigit()) advance()
        }

        val literal = sourceCode.substring(startPosition, currentPosition).toDouble()
        addToken(NUMBER, literal)
    }

    /**
     * Scans an identifier or keyword.
     * Consumes all valid identifier characters, then checks the complete lexeme against the keywords map.
     * This deferred classification ensures "orchid" is an identifier, not the keyword "or" followed by "child".
     */
    private fun identifier() {
        while (peek().isLetterOrDigit() || peek() == '_') advance()
        val literal = sourceCode.substring(startPosition, currentPosition)
        val type = keywords.getOrDefault(literal, IDENTIFIER)
        addToken(type)
    }

    private fun addToken(type: TokenType) = addToken(type, literal = null)

    private fun addToken(type: TokenType, literal: Any?) {
        val lexeme = sourceCode.substring(startPosition, currentPosition)
        tokens.add(Token(type, lexeme, literal, lineNumber))
    }

    private fun advance() = sourceCode[currentPosition++]

    /**
     * Scans a string literal enclosed in double quotes.
     * Lox strings can span multiple lines, so newlines are tracked for error reporting.
     * Literal value excludes the surrounding quotes.
     */
    private fun string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') lineNumber++
            advance()
        }

        if (isAtEnd()) {
            Lox.error(lineNumber, "Unterminated string.")
            return
        }

        advance()
        val literal = sourceCode.substring(startPosition + 1, currentPosition - 1)
        addToken(STRING, literal)
    }

    /**
     * Conditional lookahead for two-character operators.
     * Consumes the current character only if it matches expected, returning true.
     * Otherwise, leaves it for future processing and returns false.
     */
    private fun match(expected: Char): Boolean {
        if (isAtEnd()) return false
        if (sourceCode[currentPosition] != expected) return false
        currentPosition++
        return true
    }

    /**
     * Returns current character without consuming it.
     * Returns '\u0000' if at end of source.
     */
    private fun peek(): Char {
        if (isAtEnd()) return '\u0000'
        return sourceCode[currentPosition]
    }

    /**
     * Returns next character without consuming it.
     * Returns '\u0000' if beyond end. Used for decimal point lookahead.
     */
    private fun peekNext(): Char {
        if (currentPosition >= sourceCode.lastIndex) return '\u0000'
        return sourceCode[currentPosition + 1]
    }

    private fun isAtEnd() = currentPosition >= sourceCode.length
}

/**
 * Reserved keywords in Lox.
 * Used to distinguish keywords from identifiers during scanning.
 */
private val keywords = mapOf(
    "and" to AND,
    "class" to CLASS,
    "else" to ELSE,
    "false" to FALSE,
    "for" to FOR,
    "fun" to FUN,
    "if" to IF,
    "nil" to NIL,
    "or" to OR,
    "print" to PRINT,
    "return" to RETURN,
    "super" to SUPER,
    "this" to THIS,
    "true" to TRUE,
    "var" to VAR,
    "while" to WHILE,
)