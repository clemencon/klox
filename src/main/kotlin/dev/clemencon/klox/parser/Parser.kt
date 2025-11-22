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
 * statement      → exprStmt
 *                | ifStmt
 *                | printStmt
 *                | block ;
 *
 * ifStmt         → "if" "(" expression ")" statement
 *                ( "else" statement )? ;
 *
 * block          →  "{" declaration* "}" ;
 *
 * expression     → assignment ;
 * assignment     → IDENTIFIER "=" assignment
 *                | logic_or ;
 * logic_or       → logic_and ( "or" logic_and )* ;
 * logic_and      → equality ( "and" equality )* ;
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

    /** Parses source into statements. Skips declarations that fail with parse errors. */
    fun parse(): List<Statement> = buildList {
        while (!isAtEnd()) declaration()?.let { add(it) }
    }

    /** Entry point for expression parsing. Delegates to assignment (lowest precedence). */
    private fun expression(): Expression = assignment()

    /** Parses variable declarations and statements. Returns null on error after synchronizing to next statement. */
    private fun declaration(): Statement? {
        try {
            return if (match(VAR)) variableDeclaration() else statement()
        } catch (_: ParseError) {
            synchronize()
            return null
        }
    }

    /** Dispatches to the appropriate statement parser based on current token. */
    private fun statement(): Statement = when {
        match(IF) -> ifStatement()
        match(PRINT) -> printStatement()
        match(LEFT_BRACE) -> BlockStatement(blockStatement())
        else -> expressionStatement()
    }

    private fun ifStatement(): Statement {
        consume(LEFT_PAREN, "Expect '(' after 'if'.")
        val condition = expression()
        consume(RIGHT_PAREN, "Expect ')' after if condition.")

        val thenBranch = statement()
        val elseBranch = if (match(ELSE)) statement() else null

        return IfStatement(condition, thenBranch, elseBranch)
    }

    /** Parses 'print' statement: print <expression> ; */
    private fun printStatement(): Statement {
        val value = expression()
        consume(SEMICOLON, "Expect ';' after value.")
        return PrintStatement(value)
    }

    /** Parses 'var' declaration: var <name> = <expression>? ; */
    private fun variableDeclaration(): Statement {
        val name = consume(IDENTIFIER, "Expect variable name.")
        val initializer = if (match(EQUAL)) expression() else null  // null allows "var x;" without an initializer.
        consume(SEMICOLON, "Expect ';' after variable declaration.")
        return VariableDeclaration(name, initializer)
    }

    /** Parses expression statement: <expression> ; (evaluates expression and discards result). */
    private fun expressionStatement(): Statement {
        val expr = expression()
        consume(SEMICOLON, "Expect ';' after expression.")
        return ExpressionStatement(expr)
    }

    private fun blockStatement(): List<Statement> {
        val statements = buildList {
            while (!check(RIGHT_BRACE) && !isAtEnd()) {
                declaration()?.let { add(it) }
            }
        }
        consume(RIGHT_BRACE, "Expect '}' after block.")
        return statements
    }

    /** Right-associative assignment to variable identifiers. Recursion enables a = b = 5. */
    private fun assignment(): Expression {
        // The left-hand side is parsed as a normal expression first,
        // then checked for validity as an assignment target after the fact.
        // This simplifies the parser by reusing existing expression rules,
        // at the cost of a type check here to convert the r-value into an l-value representation.
        val expression = or()
        if (!match(EQUAL)) return expression

        val equals = previous() // Capture = before assignment() moves previous.
        val value = assignment()  // Right-associative: recurse for right side.
        if (expression is Variable) return Assignment(expression.name, value)

        throw error(equals, "Invalid assignment target.")
    }

    private fun or(): Expression {
        var expression = and()

        while (match(OR)) {
            val operator = previous()
            val right = and()
            expression = Logical(expression, operator, right)
        }

        return expression
    }

    private fun and(): Expression {
        var expression = equality()

        while (match(AND)) {
            val operator = previous()
            val right = equality()
            expression = Logical(expression, operator, right)
        }

        return expression
    }

    /** Left-associative == and != operators. Loop builds left-to-right: 'a == b == c' becomes '(a == b) == c'. */
    private fun equality(): Expression {
        var expression = comparison()

        while (match(BANG_EQUAL, EQUAL_EQUAL)) {
            val operator: Token = previous()
            val right = comparison()
            expression = Binary(expression, operator, right)
        }

        return expression
    }

    /** Comparison operators. Higher precedence than equality: 'a == b > c' → 'a == (b > c)'. */
    private fun comparison(): Expression {
        var expression = term()

        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            val operator = previous()
            val right = term()
            expression = Binary(expression, operator, right)
        }

        return expression
    }

    /** Addition and subtraction. */
    private fun term(): Expression {
        var expression = factor()

        while (match(MINUS, PLUS)) {
            val operator = previous()
            val right = factor()
            expression = Binary(expression, operator, right)
        }

        return expression
    }

    /** Multiplication and division. Higher precedence than addition: 'a + b * c' → 'a + (b * c)'. */
    private fun factor(): Expression {
        var expression = unary()

        while (match(SLASH, STAR)) {
            val operator = previous()
            val right = unary()
            expression = Binary(expression, operator, right)
        }

        return expression
    }

    /** Right-associative unary operators (! and -). Recursion allows stacking: '!!true'. */
    private fun unary(): Expression {
        if (match(BANG, MINUS)) {
            val operator = previous()
            val right = unary()
            return Unary(operator, right)
        }

        return primary()
    }

    /** Literals and parenthesized expressions (highest precedence). */
    private fun primary(): Expression = when {
        match(FALSE) -> Literal(false)
        match(TRUE) -> Literal(true)
        match(NIL) -> Literal(null)
        match(NUMBER, STRING) -> Literal(previous().literal)
        match(IDENTIFIER) -> Variable(previous())

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