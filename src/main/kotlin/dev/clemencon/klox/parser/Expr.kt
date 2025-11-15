package dev.clemencon.klox.parser

import dev.clemencon.klox.scanner.Token

/**
 * Expression AST. Sealed interface enables exhaustive when() matching
 * and adding operations (evaluation, type checking) without modifying these classes.
 */
sealed interface Expr

/** Literal constants: numbers, strings, booleans, nil. */
data class Literal(
    val value: Any?
) : Expr

/** Unary operators: ! and -. Token provides operator type and line number for errors. */
data class Unary(
    val operator: Token,
    val right: Expr
) : Expr

/** Binary operators: +, -, *, /, ==, !=, <, >, <=, >=. Tree structure encodes precedence: '1 + 2 * 3' → + (1, * (2, 3)). */
data class Binary(
    val left: Expr,
    val operator: Token,
    val right: Expr
) : Expr

/** Parenthesized expression. Overrides precedence: '(2 + 3) * 4' forces addition first. */
data class Grouping(
    val expr: Expr
) : Expr
