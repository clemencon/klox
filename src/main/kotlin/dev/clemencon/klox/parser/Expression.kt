package dev.clemencon.klox.parser

import dev.clemencon.klox.scanner.Token

/**
 * AST representation for Lox expressions.
 * Models expressions as a tree where leaf nodes are literals and interior nodes are operators with operand subtrees.
 * Sealed interface enables exhaustive pattern matching,
 * allowing new operations (evaluation, type checking, pretty printing) without modifying expression classes.
 */
sealed interface Expression

/**
 * Literal value expression.
 * Leaf nodes representing constants: numbers, strings, booleans, nil.
 * Evaluate to themselves.
 */
data class Literal(
    val value: Any?
) : Expression

/**
 * Unary operator expression.
 * Prefix operators: logical NOT (!) and arithmetic negation (-).
 * Operator token contains type and source location for error reporting.
 */
data class Unary(
    val operator: Token,
    val right: Expression
) : Expression

/**
 * Binary operator expression.
 * Infix operators: arithmetic (+, -, *, /), comparison (==, !=, <, >, <=, >=), and logical (and, or).
 * Tree structure encodes precedence: "1 + 2 * 3" has multiplication as a subtree of addition,
 * ensuring it evaluates first.
 */
data class Binary(
    val left: Expression,
    val operator: Token,
    val right: Expression
) : Expression

/**
 * Grouping expression (parenthesized subexpression).
 * Allows explicit control over evaluation order. "(2 + 3) * 4" forces addition before multiplication.
 */
data class Grouping(
    val expression: Expression
) : Expression
