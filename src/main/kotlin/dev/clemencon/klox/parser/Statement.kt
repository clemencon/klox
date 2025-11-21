package dev.clemencon.klox.parser

import dev.clemencon.klox.scanner.Token

/**
 * Statement AST.
 * Sealed interface enables exhaustive matching when adding operations without modifying these classes.
 */
sealed interface Statement

/** Evaluates expression and discards result. */
data class ExpressionStatement(val expression: Expression) : Statement

/** Evaluates expression and prints result. */
data class PrintStatement(val expression: Expression) : Statement

/** Binds identifier to optional initial value. */
data class VariableDeclaration(val name: Token, val initializer: Expression?) : Statement

/** Series of statements or declarations. */
data class BlockStatement(val statements: List<Statement>) : Statement