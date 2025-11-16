package dev.clemencon.klox.parser

import dev.clemencon.klox.scanner.Token

/**
 * Statement AST.
 * Sealed interface enables exhaustive matching when adding operations without modifying these classes.
 */
sealed interface Stmt

/** Expression statement: evaluates expression and discards result. */
data class Expression(val expression: Expr) : Stmt

/** Print statement: evaluates expression and prints result. */
data class Print(val expression: Expr) : Stmt

/** Variable declaration: binds identifier to optional initial value. */
data class Var(val name: Token, val initializer: Expr?) : Stmt