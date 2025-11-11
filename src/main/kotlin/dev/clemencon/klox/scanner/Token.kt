package dev.clemencon.klox.scanner

/**
 * A single meaningful unit of source code.
 * Scanned from raw text during lexical analysis. Contains information about what was found,
 * where it appeared, and its runtime value (if applicable).
 *
 * @property type Classification (keyword, operator, literal, etc.) for quick parsing.
 * @property lexeme Raw substring from source code, preserving exact characters for errors.
 * @property literal Runtime value for number/string literals; null for keywords and operators.
 * @property lineNumber Source line location for error reporting.
 */
data class Token(
    val type: TokenType,
    val lexeme: String,
    val literal: Any?,
    val lineNumber: Int,
) {
    override fun toString() = "$type $lexeme $literal"
}