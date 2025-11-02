package dev.clemencon.klox.scanner

import dev.clemencon.klox.Lox
import dev.clemencon.klox.scanner.TokenType.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertEquals

class SingleCharacterTokensTest {

    @BeforeEach
    fun setUp() {
        // Reset error flag before each test
        Lox.hadError = false
    }

    @ParameterizedTest
    @MethodSource("singleCharacterTokenProvider")
    fun `single character token is scanned correctly`(sourceCode: String, expectedType: TokenType) {
        val tokens = scan(sourceCode)

        tokens.assertAmount(2)
        tokens.assertEndsWithEof()
        assertEquals(
            expected = Token(type = expectedType, lexeme = sourceCode, literal = null, lineNumber = 1),
            actual = tokens.first()
        )
    }

    companion object {
        @JvmStatic
        fun singleCharacterTokenProvider() = listOf(
            "(" to LEFT_PAREN,
            ")" to RIGHT_PAREN,
            "{" to LEFT_BRACE,
            "}" to RIGHT_BRACE,
            "," to COMMA,
            "." to DOT,
            "-" to MINUS,
            "+" to PLUS,
            ";" to SEMICOLON,
            "*" to STAR,
            "/" to SLASH
        ).map { (lexeme, type) -> Arguments.of(lexeme, type) }
    }
}
