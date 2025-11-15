package dev.clemencon.klox.scanner

/**
 * Lexical token from scanner.
 * @property type Classification (NUMBER, PLUS, PRINT, etc.).
 * @property lexeme Raw source text (preserved for error messages).
 * @property literal Parsed value for number/string literals; null for keywords/operators.
 * @property lineNumber For error reporting.
 */
data class Token(val type: TokenType, val lexeme: String, val literal: Any?, val lineNumber: Int) {
    override fun toString() = "$type $lexeme $literal"
}