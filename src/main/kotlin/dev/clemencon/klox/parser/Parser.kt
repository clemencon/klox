package dev.clemencon.klox.parser

import dev.clemencon.klox.scanner.Token
import dev.clemencon.klox.scanner.TokenType
import dev.clemencon.klox.scanner.TokenType.*

class Parser(private val tokens: List<Token>) {
    private var currentPosition: Int = 0

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
        var expression: Expression = factor()

        while (match(MINUS, PLUS)) {
            val operator = previous()
            val right: Expression = factor()
            expression = Binary(expression, operator, right)
        }

        return expression
    }

    private fun match(vararg tokenType: TokenType): Boolean {
        tokenType.forEach {
            if (check(it)) {
                advance();
                return true
            }
        }

        return false;
    }

    private fun check(tokenType: TokenType): Boolean {
        if (isAtEnd()) return false
        return peek().type == tokenType
    }

    // ju: Would it make sense to use a custom data type/collection?

    private fun advance(): Token {
        if (isAtEnd()) currentPosition++
        return previous();
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

}