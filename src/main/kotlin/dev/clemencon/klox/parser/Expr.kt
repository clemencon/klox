package dev.clemencon.klox.parser

import dev.clemencon.klox.scanner.Token

/**
 * Expression AST.
 * Sealed interface enables exhaustive matching when adding operations without modifying these classes.
 */
sealed interface Expr

/** Literal constants: numbers, strings, booleans, nil. */
data class Literal(val value: Any?) : Expr

/** Unary operators: ! and -. Token provides operator type and line number for errors. */
data class Unary(val operator: Token, val right: Expr) : Expr

/** Binary operators: +, -, *, /, ==, !=, <, >, <=, >=. Tree structure encodes precedence. */
data class Binary(val left: Expr, val operator: Token, val right: Expr) : Expr

/** Parenthesized expression. Overrides precedence: '(2 + 3) * 4' forces addition first. */
data class Grouping(val expr: Expr) : Expr

/** Variable reference: reads value from identifier. */
data class Variable(val name: Token) : Expr