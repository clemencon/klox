package dev.clemencon.klox.parser

import dev.clemencon.klox.scanner.Token

/**
 * Expression AST.
 * Sealed interface enables exhaustive matching when adding operations without modifying these classes.
 */
sealed interface Expression

/** Literal constants: numbers, strings, booleans, nil. */
data class Literal(val value: Any?) : Expression

/** Unary operators: ! and -. Token provides operator type and line number for errors. */
data class Unary(val operator: Token, val right: Expression) : Expression

data class Logical(val left: Expression, val operator: Token, val right: Expression) : Expression

/** Binary operators: +, -, *, /, ==, !=, <, >, <=, >=. Tree structure encodes precedence. */
data class Binary(val left: Expression, val operator: Token, val right: Expression) : Expression

data class Call(val callee: Expression, val closingParen: Token, val arguments: List<Expression>) : Expression

/** Parenthesized expression. Overrides precedence: '(2 + 3) * 4' forces addition first. */
data class Grouping(val expression: Expression) : Expression

/** Variable reference: reads value from identifier. */
data class Variable(val name: Token) : Expression

/** Assignment to a variable identifier. Returns the value, enabling a = b = 5. */
data class Assignment(val name: Token, val value: Expression) : Expression