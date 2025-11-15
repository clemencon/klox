package dev.clemencon.klox.parser

/** Statement AST. Sealed interface enables exhaustive when() matching. */
sealed interface Stmt

/** Expression statement: evaluates expression and discards result. */
data class Expression(val expression: Expr) : Stmt

/** Print statement: evaluates expression and prints result. */
data class Print(val expression: Expr) : Stmt