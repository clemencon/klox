package dev.clemencon.klox.interpreter

import dev.clemencon.klox.parser.Binary
import dev.clemencon.klox.parser.Expr
import dev.clemencon.klox.parser.Grouping
import dev.clemencon.klox.parser.Literal
import dev.clemencon.klox.parser.Unary

// ju: Move to parser/expression/

/**
 * Produces Lisp-style string representation for debugging.
 * Makes tree structure explicit: "1 + 2 * 3" becomes "(+ 1 (* 2 3))",
 * revealing that multiplication is a subtree of addition.
 */
fun Expr.toString() = when (this) {
    is Binary -> parenthesize(this.operator.lexeme, this.left, this.right)
    is Grouping -> parenthesize("group", this.expr)
    is Literal -> if (this.value == null) "nil" else this.value.toString()
    is Unary -> parenthesize(this.operator.lexeme, this.right)
}

/**
 * Formats operator and operands in parenthesized notation: (operator expr1 expr2 ...).
 */
private fun parenthesize(name: String, vararg expr: Expr): String = buildString {
    append("($name")
    expr.forEach { append(" $it") }
    append(")")
}
