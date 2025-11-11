package dev.clemencon.klox.parser

import dev.clemencon.klox.scanner.Token

sealed interface Expression

data class Literal(
    val value: Any?
) : Expression

data class Unary(
    val operator: Token,
    val right: Expression
) : Expression

data class Binary(
    val left: Expression,
    val operator: Token,
    val right: Expression
) : Expression

data class Grouping(
    val expression: Expression
) : Expression

// The print function will be removed.

fun Expression.toString() = when (this) {
    is Binary -> parenthesize(this.operator.lexeme, this.left, this.right)
    is Grouping -> parenthesize("group", this.expression)
    is Literal -> if (this.value == null) "nil" else this.value.toString()
    is Unary -> parenthesize(this.operator.lexeme, this.right)
}

fun parenthesize(name: String, vararg expression: Expression): String = buildString {
    append("($name")
    expression.forEach { append(" ${it.toString()}") }
    append(")")
}
