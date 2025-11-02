package dev.clemencon.klox.scanner

import dev.clemencon.klox.Lox
import dev.clemencon.klox.scanner.TokenType.*

/**
 * Converts source code into tokens.
 */
class Scanner(private val sourceCode: String) {
    private val tokens = mutableListOf<Token>()

    // StartPosition marks where the current lexeme begins.
    // CurrentPosition is the next character to consume.
    private var startPosition = 0
    private var currentPosition = 0
    private var lineNumber = 1

    /**
     * Scans the source and returns tokens, with EOF appended.
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
     * Scans a single token. Errors are reported but don't stop scanning.
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

            // '//' starts a comment that runs to end-of-line.
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

            // Report unrecognized characters but keep scanning.
            else -> Lox.error(lineNumber, "Unexpected character.")
        }
    }

    /**
     * Scans a number literal. Supports decimal numbers.
     */
    private fun number() {
        while (peek().isDigit()) advance()

        // Look for a fractional part.
        if (peek() == '.' && peekNext().isDigit()) {
            advance()
            while (peek().isDigit()) advance()
        }

        val literal = sourceCode.substring(startPosition, currentPosition).toDouble()
        addToken(NUMBER, literal)
    }

    /**
     * Scans reserved words and identifiers.
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
     * Scans a string literal. Supports multi-line strings.
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
     * Consumes the current character only if it matches expected.
     */
    private fun match(expected: Char): Boolean {
        if (isAtEnd()) return false
        if (sourceCode[currentPosition] != expected) return false
        currentPosition++
        return true
    }

    /**
     * Returns the current character without consuming it.
     */
    private fun peek(): Char {
        if (isAtEnd()) return '\u0000'
        return sourceCode[currentPosition]
    }

    /**
     * Returns the next character without consuming anything.
     */
    private fun peekNext(): Char {
        if (currentPosition >= sourceCode.lastIndex) return '\u0000'
        return sourceCode[currentPosition + 1]
    }

    private fun isAtEnd() = currentPosition >= sourceCode.length
}

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