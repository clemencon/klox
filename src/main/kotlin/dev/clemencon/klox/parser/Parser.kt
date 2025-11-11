package dev.clemencon.klox.parser

import dev.clemencon.klox.Lox
import dev.clemencon.klox.scanner.Token
import dev.clemencon.klox.scanner.TokenType
import dev.clemencon.klox.scanner.TokenType.*

class Parser(private val tokens: List<Token>) {
    private var currentPosition: Int = 0

    fun parse(): Expression? {
        return try {
            expression()
        } catch (_: ParseError) {
            null
        }
    }

    private fun expression(): Expression {
        return equality()
    }

    private fun equality(): Expression {
        var expression = comparison()

        while (match(BANG_EQUAL, EQUAL_EQUAL)) {
            val operator: Token = previous()
            val right = comparison()
            expression = Binary(expression, operator, right)
        }

        return expression
    }


    private fun comparison(): Expression {
        var expression = term()

        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            val operator = previous()
            val right = term()
            expression = Binary(expression, operator, right)
        }

        return expression
    }

    private fun term(): Expression {
        var expression = factor()

        while (match(MINUS, PLUS)) {
            val operator = previous()
            val right = factor()
            expression = Binary(expression, operator, right)
        }

        return expression
    }

    private fun factor(): Expression {
        var expression = unary()

        while (match(SLASH, STAR)) {
            val operator = previous()
            val right = unary()
            expression = Binary(expression, operator, right)
        }

        return expression
    }

    private fun unary(): Expression {
        if (match(BANG, MINUS)) {
            val operator = previous()
            val right = unary()
            return Unary(operator, right)
        }

        return primary()
    }

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

    private fun match(vararg tokenType: TokenType): Boolean {
        tokenType.forEach {
            if (check(it)) {
                advance()
                return true
            }
        }

        return false
    }

    private fun consume(tokenType: TokenType, message: String): Token {
        if (check(tokenType)) return advance()
        throw error(peek(), message)
    }

    private fun check(tokenType: TokenType): Boolean {
        if (isAtEnd()) return false
        return peek().type == tokenType
    }

    // ju: Would it make sense to use a custom data type/collection?

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

    private fun previous(): Token {
        return tokens.get(currentPosition - 1)
    }

    private fun error(token: Token, message: String): ParseError {
        Lox.error(token, message)
        return ParseError()
    }

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

class ParseError : RuntimeException()