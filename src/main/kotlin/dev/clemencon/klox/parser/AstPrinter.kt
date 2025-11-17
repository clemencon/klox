package dev.clemencon.klox.parser

/** String representation for debugging: '1 + 2 * 3' → '(+ 1 (* 2 3))'. */
fun Expr.toString() = when (this) {
    is Binary -> parenthesize(this.operator.lexeme, this.left, this.right)
    is Grouping -> parenthesize("group", this.expr)
    is Literal -> if (this.value == null) "nil" else this.value.toString()
    is Unary -> parenthesize(this.operator.lexeme, this.right)
    is Variable -> TODO()
}

/** Formats as: (operator expr1 expr2 ...) */
private fun parenthesize(name: String, vararg expr: Expr): String = buildString {
    append("($name")
    expr.forEach { append(" $it") }
    append(")")
}