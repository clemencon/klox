package dev.clemencon.klox.scanner

import dev.clemencon.klox.Lox
import dev.clemencon.klox.scanner.TokenType.*

/**
 * Lexical scanner. Uses two pointers to track lexemes:
 * - startPosition: where current lexeme begins
 * - currentPosition: character being examined
 */
class Scanner(private val sourceCode: String) {
    private val tokens = mutableListOf<Token>()

    private var startPosition = 0
    private var currentPosition = 0
    private var lineNumber = 1

    /** Scans source into tokens, resetting startPosition before each lexeme. */
    fun scanTokens(): List<Token> {
        while (!isAtEnd()) {
            startPosition = currentPosition
            scanToken()
        }

        tokens.add(Token(type = EOF, lexeme = "", literal = null, lineNumber))
        return tokens
    }

    /** Recognizes and emits a single token. Multi-character operators use match() for lookahead. */
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

    /** Number literals. Consumes '.' only when followed by a digit: '123.toString()' → '123' + '.' + 'toString'. */
    private fun number() {
        while (peek().isDigit()) advance()

        if (peek() == '.' && peekNext().isDigit()) {
            advance()
            while (peek().isDigit()) advance()
        }

        val literal = sourceCode.substring(startPosition, currentPosition).toDouble()
        addToken(NUMBER, literal)
    }

    /** Identifiers and keywords. Checks full lexeme after consuming: 'orchid' is not 'or' + 'chid'. */
    private fun identifier() {
        while (peek().isLetterOrDigit() || peek() == '_') advance()
        val literal = sourceCode.substring(startPosition, currentPosition)
        val type = keywords.getOrDefault(literal, IDENTIFIER)
        addToken(type)
    }

    private fun addToken(type: TokenType) = addToken(type, literal = null)

    /** Creates token from current lexeme (startPosition to currentPosition). */
    private fun addToken(type: TokenType, literal: Any?) {
        val lexeme = sourceCode.substring(startPosition, currentPosition)
        tokens.add(Token(type, lexeme, literal, lineNumber))
    }

    /** Consumes and returns current character. */
    private fun advance() = sourceCode[currentPosition++]

    /** String literals. Can span multiple lines; newlines are tracked for error reporting. */
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

    /** Conditional lookahead: consumes current character only if it matches expected. */
    private fun match(expected: Char): Boolean {
        if (isAtEnd()) return false
        if (sourceCode[currentPosition] != expected) return false
        currentPosition++
        return true
    }

    /** Returns current character without consuming. Returns '\u0000' at EOF. */
    private fun peek(): Char = if (isAtEnd()) '\u0000' else sourceCode[currentPosition]

    /** Two-character lookahead for decimal points. Returns '\u0000' at EOF. */
    private fun peekNext(): Char =
        if (currentPosition >= sourceCode.lastIndex) '\u0000' else sourceCode[currentPosition + 1]

    private fun isAtEnd() = currentPosition >= sourceCode.length
}

/** Reserved keywords. */
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