package dev.clemencon.klox.interpreter

import dev.clemencon.klox.parser.Binary
import dev.clemencon.klox.parser.Expression
import dev.clemencon.klox.parser.Grouping
import dev.clemencon.klox.parser.Literal
import dev.clemencon.klox.parser.Unary

/**
 * Produces Lisp-style string representation for debugging.
 * Makes tree structure explicit: "1 + 2 * 3" becomes "(+ 1 (* 2 3))",
 * revealing that multiplication is a subtree of addition.
 */
fun Expression.toString() = when (this) {
    is Binary -> parenthesize(this.operator.lexeme, this.left, this.right)
    is Grouping -> parenthesize("group", this.expression)
    is Literal -> if (this.value == null) "nil" else this.value.toString()
    is Unary -> parenthesize(this.operator.lexeme, this.right)
}

/**
 * Formats operator and operands in parenthesized notation: (operator expr1 expr2 ...).
 */
private fun parenthesize(name: String, vararg expression: Expression): String = buildString {
    append("($name")
    expression.forEach { append(" $it") }
    append(")")
}
