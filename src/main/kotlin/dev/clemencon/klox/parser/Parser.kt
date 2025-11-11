package dev.clemencon.klox.parser

import dev.clemencon.klox.Lox
import dev.clemencon.klox.scanner.Token
import dev.clemencon.klox.scanner.TokenType
import dev.clemencon.klox.scanner.TokenType.*

/**
 * Recursive descent parser for Lox expressions.
 * Transforms tokens into an AST where each grammar rule becomes a method.
 * Grammar structure encodes operator precedence by nesting:
 * lower precedence operators at the top call higher precedence levels.
 *
 * Grammar (lowest to highest precedence):
 * expression → equality ;
 * equality   → comparison ( ( "!=" | "==" ) comparison )* ;
 * comparison → term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
 * term       → factor ( ( "-" | "+" ) factor )* ;
 * factor     → unary ( ( "/" | "*" ) unary )* ;
 * unary      → ( "!" | "-" ) unary | primary ;
 * primary    → NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")" ;
 */
class Parser(private val tokens: List<Token>) {
    /** Points to the next token to consume. */
    private var currentPosition: Int = 0

    /**
     * Parses tokens into an expression AST.
     * Returns null if parsing fails, errors are reported via Lox.error().
     */
    fun parse(): Expression? {
        return try {
            expression()
        } catch (_: ParseError) {
            null
        }
    }

    /**
     * Entry point for expression parsing.
     * Delegates to equality (lowest precedence). Exists for future expansion (e.g., comma operator).
     */
    private fun expression(): Expression {
        return equality()
    }

    /**
     * Parses equality operators (== and !=).
     * Builds left-associative tree by iteratively consuming operators and wrapping in Binary nodes.
     */
    private fun equality(): Expression {
        var expression = comparison()

        while (match(BANG_EQUAL, EQUAL_EQUAL)) {
            val operator: Token = previous()
            val right = comparison()
            expression = Binary(expression, operator, right)
        }

        return expression
    }

    /**
     * Parses comparison operators (>, >=, <, <=).
     * Higher precedence than equality, so 'a == b > c' parses as 'a == (b > c)'.
     */
    private fun comparison(): Expression {
        var expression = term()

        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            val operator = previous()
            val right = term()
            expression = Binary(expression, operator, right)
        }

        return expression
    }

    /**
     * Parses additive operators (+ and -).
     * Higher precedence than comparison.
     */
    private fun term(): Expression {
        var expression = factor()

        while (match(MINUS, PLUS)) {
            val operator = previous()
            val right = factor()
            expression = Binary(expression, operator, right)
        }

        return expression
    }

    /**
     * Parses multiplicative operators (* and /).
     * Higher precedence than addition, so 'a + b * c' parses as 'a + (b * c)'.
     */
    private fun factor(): Expression {
        var expression = unary()

        while (match(SLASH, STAR)) {
            val operator = previous()
            val right = unary()
            expression = Binary(expression, operator, right)
        }

        return expression
    }

    /**
     * Parses unary operators (! and -).
     * Right-associative. Uses recursion to allow stacking (e.g., '!!true', '--5').
     */
    private fun unary(): Expression {
        if (match(BANG, MINUS)) {
            val operator = previous()
            val right = unary()
            return Unary(operator, right)
        }

        return primary()
    }

    /**
     * Parses primary expressions (literals and grouping).
     * Highest precedence level. Handles literals and parenthesized expressions.
     */
    private fun primary(): Expression {
        if (match(FALSE)) return Literal(false)
        if (match(TRUE)) return Literal(true)
        if (match(NIL)) return Literal(null)

        if (match(NUMBER, STRING)) return Literal(previous().literal)

        if (match(LEFT_PAREN)) {
            val expression = expression()
            consume(RIGHT_PAREN, "Expect ')' after expression.")
            return Grouping(expression)
        }

        throw error(peek(), "Expect expression.")
    }

    /**
     * Checks if current token matches any given types and consumes it if so.
     * Returns true if matched and consumed, false otherwise.
     */
    private fun match(vararg tokenType: TokenType): Boolean {
        tokenType.forEach {
            if (check(it)) {
                advance()
                return true
            }
        }

        return false
    }

    /**
     * Consumes expected token type or throws error.
     * Unlike match(), this enforces the expectation rather than just checking.
     */
    private fun consume(tokenType: TokenType, message: String): Token {
        if (check(tokenType)) return advance()
        throw error(peek(), message)
    }

    /**
     * Checks if current token matches type without consuming it.
     * Returns false at EOF to avoid treating EOF as matching real token types.
     */
    private fun check(tokenType: TokenType): Boolean {
        if (isAtEnd()) return false
        return peek().type == tokenType
    }

    /**
     * Consumes and returns current token, advancing position.
     * Increments currentPosition before returning, but returns the token at the old position via previous().
     * Ensures currentPosition points to next unconsumed token.
     */
    private fun advance(): Token {
        if (!isAtEnd()) currentPosition++
        return previous()
    }

    private fun isAtEnd(): Boolean {
        return peek().type == EOF
    }

    private fun peek(): Token {
        return tokens[currentPosition]
    }

    /**
     * Returns the most recently consumed token.
     * Used after match() or advance() to get the token just consumed (e.g., for Binary nodes).
     */
    private fun previous(): Token {
        return tokens.get(currentPosition - 1)
    }

    /**
     * Reports error and returns ParseError exception for control flow.
     * The exception unwinds the parser back to the try-catch in parse().
     */
    private fun error(token: Token, message: String): ParseError {
        Lox.error(token, message)
        return ParseError()
    }

    /**
     * Discards tokens until reaching a statement boundary (panic mode recovery).
     * Looks for semicolons or statement-starting keywords (class, fun, var, etc.).
     * Currently unused; will be important when parsing statements.
     */
    private fun synchronize() {
        advance()

        while (!isAtEnd()) {
            if (previous().type == SEMICOLON) return

            when (peek().type) {
                CLASS, FUN, VAR, FOR, IF, WHILE, PRINT, RETURN -> return
                else -> advance()
            }
        }
    }
}

/**
 * Sentinel exception for parser control flow.
 * Used internally to unwind the call stack when parse errors are detected.
 * Caught in parse() and doesn't extend beyond the parser.
 * Actual error reporting happens via Lox.error() before throwing this.
 *
 * Using an exception for control flow allows:
 * 1. Immediate exit from deeply nested parsing methods.
 * 2. Clean Expression return types without error wrapping.
 * 3. Common pattern in recursive descent parsers for panic mode recovery.
 */
class ParseError : RuntimeException()