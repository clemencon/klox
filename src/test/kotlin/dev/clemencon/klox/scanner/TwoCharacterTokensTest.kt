package dev.clemencon.klox.scanner

import dev.clemencon.klox.Lox
import dev.clemencon.klox.scanner.TokenType.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertEquals

class TwoCharacterTokensTest {

    @BeforeEach
    fun setUp() {
        // Reset error flag before each test
        Lox.hadError = false
    }

    @ParameterizedTest
    @MethodSource("singleCharacterOperatorProvider")
    fun `single character operator produces correct token`(sourceCode: String, expectedType: TokenType) {
        val tokens = scan(sourceCode)

        tokens.assertAmount(2)
        tokens.assertEndsWithEof()
        assertEquals(
            expected = Token(type = expectedType, lexeme = sourceCode, literal = null, lineNumber = 1),
            actual = tokens.first()
        )
    }

    @ParameterizedTest
    @MethodSource("doubleCharacterOperatorProvider")
    fun `two character operator produces single token not two separate tokens`(
        sourceCode: String,
        expectedType: TokenType
    ) {
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
        fun singleCharacterOperatorProvider() = listOf(
            "!" to BANG,
            "=" to EQUAL,
            "<" to LESS,
            ">" to GREATER
        ).map { (lexeme, type) -> Arguments.of(lexeme, type) }

        @JvmStatic
        fun doubleCharacterOperatorProvider() = listOf(
            "!=" to BANG_EQUAL,
            "==" to EQUAL_EQUAL,
            "<=" to LESS_EQUAL,
            ">=" to GREATER_EQUAL
        ).map { (lexeme, type) -> Arguments.of(lexeme, type) }
    }
}
