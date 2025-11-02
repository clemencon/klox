package dev.clemencon.klox.scanner

import dev.clemencon.klox.Lox
import dev.clemencon.klox.scanner.TokenType.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertEquals

class KeywordsTest {

    @BeforeEach
    fun setUp() {
        // Reset error flag before each test
        Lox.hadError = false
    }

    @ParameterizedTest
    @MethodSource("keywordProvider")
    fun `keyword produces correct token type`(sourceCode: String, expectedType: TokenType) {
        val tokens = scan(sourceCode)

        tokens.assertAmount(2)
        tokens.assertEndsWithEof()
        assertEquals(
            expected = Token(type = expectedType, lexeme = sourceCode, literal = null, lineNumber = 1),
            actual = tokens.first()
        )
    }

    @ParameterizedTest
    @MethodSource("caseSensitivityProvider")
    fun `capitalized or uppercase keyword produces IDENTIFIER not keyword token`(sourceCode: String) {
        val tokens = scan(sourceCode)

        tokens.assertAmount(2)
        tokens.assertEndsWithEof()
        assertEquals(
            expected = Token(type = IDENTIFIER, lexeme = sourceCode, literal = null, lineNumber = 1),
            actual = tokens.first()
        )
    }

    companion object {
        @JvmStatic
        fun keywordProvider() = listOf(
            "and" to AND,
            "class" to CLASS,
            "else" to ELSE,
            "false" to FALSE,
            "for" to FOR,
            "fun" to FUN,
            "if" to IF,
            "nil" to NIL,
            "or" to OR,
            "print" to PRINT,
            "return" to RETURN,
            "super" to SUPER,
            "this" to THIS,
            "true" to TRUE,
            "var" to VAR,
            "while" to WHILE
        ).map { (lexeme, type) -> Arguments.of(lexeme, type) }

        @JvmStatic
        fun caseSensitivityProvider() = listOf(
            "True", "False", "FALSE", "Class", "AND", "OR"
        ).map { Arguments.of(it) }
    }
}
