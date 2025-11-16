package dev.clemencon.klox.parser

import dev.clemencon.klox.Lox
import dev.clemencon.klox.scanner.Token
import dev.clemencon.klox.scanner.TokenType
import dev.clemencon.klox.scanner.TokenType.*

/**
 * Recursive descent parser. Each grammar rule becomes a method.
 * Precedence is encoded by nesting: lower precedence rules call higher ones.
 *
 * program        → declaration* EOF ;
 *
 * declaration    → varDecl
 *                | statement ;
 * varDecl        → "var" IDENTIFIER ( "=" expression )? ";" ;
 *
 * statement      → exprStmt | printStmt ;
 *
 * expression     → equality ;
 * equality       → comparison ( ( "!=" | "==" ) comparison )* ;
 * comparison     → term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
 * term           → factor ( ( "-" | "+" ) factor )* ;
 * factor         → unary ( ( "/" | "*" ) unary )* ;
 * unary          → ( "!" | "-" ) unary | primary ;
 * primary        → "true" | "false" | "nil"
 *                | NUMBER | STRING
 *                | "(" expression ")"
 *                | IDENTIFIER ;
 */
class Parser(private val tokens: List<Token>) {
    /** Points to the next token to consume. */
    private var currentPosition: Int = 0

    /** Parses a sequence of statements until EOF. */
    fun parse(): List<Stmt> = buildList { while (!isAtEnd()) add(statement()) }

    /** Entry point for expression parsing. Delegates to the lowest precedence level. */
    private fun expression(): Expr = equality()

    /** Dispatches to the appropriate statement parser based on current token. */
    private fun statement(): Stmt = when {
        match(PRINT) -> printStatement()
        else -> expressionStatement()
    }

    /** Parses 'print' statement: print <expr> ; */
    private fun printStatement(): Stmt {
        val value = expression()
        consume(SEMICOLON, "Expect ';' after value.")
        return Print(value)
    }

    /** Parses expression statement: <expr> ; (evaluates expression and discards result). */
    private fun expressionStatement(): Stmt {
        val expr = expression()
        consume(SEMICOLON, "Expect ';' after expression.")
        return Expression(expr)
    }

    /** Left-associative == and != operators. */
    private fun equality(): Expr {
        var expression = comparison()

        while (match(BANG_EQUAL, EQUAL_EQUAL)) {
            val operator: Token = previous()
            val right = comparison()
            expression = Binary(expression, operator, right)
        }

        return expression
    }

    /** Comparison operators. Higher precedence than equality: 'a == b > c' → 'a == (b > c)'. */
    private fun comparison(): Expr {
        var expression = term()

        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            val operator = previous()
            val right = term()
            expression = Binary(expression, operator, right)
        }

        return expression
    }

    /** Addition and subtraction. */
    private fun term(): Expr {
        var expression = factor()

        while (match(MINUS, PLUS)) {
            val operator = previous()
            val right = factor()
            expression = Binary(expression, operator, right)
        }

        return expression
    }

    /** Multiplication and division. Higher precedence than addition: 'a + b * c' → 'a + (b * c)'. */
    private fun factor(): Expr {
        var expression = unary()

        while (match(SLASH, STAR)) {
            val operator = previous()
            val right = unary()
            expression = Binary(expression, operator, right)
        }

        return expression
    }

    /** Right-associative unary operators (! and -). Recursion allows stacking: '!!true'. */
    private fun unary(): Expr {
        if (match(BANG, MINUS)) {
            val operator = previous()
            val right = unary()
            return Unary(operator, right)
        }

        return primary()
    }

    /** Literals and parenthesized expressions (highest precedence). */
    private fun primary(): Expr = when {
        match(FALSE) -> Literal(false)
        match(TRUE) -> Literal(true)
        match(NIL) -> Literal(null)
        match(NUMBER, STRING) -> Literal(previous().literal)

        match(LEFT_PAREN) -> {
            val expression = expression()
            consume(RIGHT_PAREN, "Expect ')' after expression.")
            Grouping(expression)
        }

        else -> throw error(peek(), "Expect expression.")
    }

    /** Consumes current token if it matches any given type. Returns true if matched. */
    private fun match(vararg tokenType: TokenType): Boolean {
        tokenType.forEach {
            if (check(it)) {
                advance()
                return true
            }
        }

        return false
    }

    /** Like match() but throws an error if token type doesn't match. */
    private fun consume(tokenType: TokenType, message: String): Token =
        if (check(tokenType)) advance() else throw error(peek(), message)

    /** Checks if current token matches type without consuming. Returns false at EOF. */
    private fun check(tokenType: TokenType): Boolean = if (isAtEnd()) false else peek().type == tokenType

    /** Consumes and returns current token. */
    private fun advance(): Token {
        if (!isAtEnd()) currentPosition++
        return previous()
    }

    /** Checks if we've reached the EOF token. */
    private fun isAtEnd(): Boolean = peek().type == EOF

    /** Returns current token without consuming. */
    private fun peek(): Token = tokens[currentPosition]

    /** Returns most recently consumed token. */
    private fun previous(): Token = tokens[currentPosition - 1]

    /** Reports error and returns ParseError to unwind the parser. */
    private fun error(token: Token, message: String): ParseError {
        Lox.error(token, message)
        return ParseError()
    }

    /** Panic mode recovery: discards tokens until reaching a statement boundary. */
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
 * Sentinel exception for unwinding the parser on errors.
 * Error reporting happens via Lox.error() before throwing this.
 * Using an exception allows immediate exit from nested parsing methods
 * without wrapping return types in Result-style containers.
 */
class ParseError : RuntimeException()