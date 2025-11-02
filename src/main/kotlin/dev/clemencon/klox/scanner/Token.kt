package dev.clemencon.klox.scanner

/**
 * A lexical token from Lox source code.
 */
data class Token(
    val type: TokenType,
    val lexeme: String,
    val literal: Any?,
    val lineNumber: Int,
) {
    override fun toString() = "$type $lexeme $literal"
}