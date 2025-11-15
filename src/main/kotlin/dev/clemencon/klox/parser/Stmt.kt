package dev.clemencon.klox.parser

sealed interface Stmt

data class Expression(val expression: Expr) : Stmt

data class Print(val expression: Expr) : Stmt