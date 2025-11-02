package dev.clemencon.klox.scanner

import dev.clemencon.klox.Lox
import dev.clemencon.klox.scanner.TokenType.*
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals

class NumberLiteralsTest {

    @BeforeEach
    fun setUp() {
        // Reset error flag before each test
        Lox.hadError = false
    }

    @Test
    fun `integer produces NUMBER token with double value`() {
        val tokens = scan(sourceCode = "123")

        tokens.assertAmount(2)
        tokens.assertEndsWithEof()
        assertEquals(
            expected = Token(type = NUMBER, lexeme = "123", literal = 123.0, lineNumber = 1),
            actual = tokens.first()
        )
    }

    @Test
    fun `zero produces NUMBER token with value zero`() {
        val tokens = scan(sourceCode = "0")

        tokens.assertAmount(2)
        tokens.assertEndsWithEof()
        assertEquals(
            expected = Token(type = NUMBER, lexeme = "0", literal = 0.0, lineNumber = 1),
            actual = tokens.first()
        )
    }

    @Test
    fun `decimal number produces NUMBER token with fractional value`() {
        val tokens = scan(sourceCode = "123.456")

        tokens.assertAmount(2)
        tokens.assertEndsWithEof()
        assertEquals(
            expected = Token(type = NUMBER, lexeme = "123.456", literal = 123.456, lineNumber = 1),
            actual = tokens.first()
        )
    }

    @Test
    fun `leading dot is not parsed as number`() {
        val tokens = scan(sourceCode = ".123")

        tokens.assertAmount(3) // DOT, NUMBER, EOF
        assertEquals(
            expected = Token(type = DOT, lexeme = ".", literal = null, lineNumber = 1),
            actual = tokens[0]
        )
        assertEquals(
            expected = Token(type = NUMBER, lexeme = "123", literal = 123.0, lineNumber = 1),
            actual = tokens[1]
        )
    }

    @Test
    fun `trailing dot produces NUMBER token then DOT token`() {
        val tokens = scan(sourceCode = "123.")

        tokens.assertAmount(3) // NUMBER, DOT, EOF
        assertEquals(
            expected = Token(type = NUMBER, lexeme = "123", literal = 123.0, lineNumber = 1),
            actual = tokens[0]
        )
        assertEquals(
            expected = Token(type = DOT, lexeme = ".", literal = null, lineNumber = 1),
            actual = tokens[1]
        )
    }

    @Test
    fun `zero point zero produces NUMBER token`() {
        val tokens = scan(sourceCode = "0.0")

        tokens.assertAmount(2)
        tokens.assertEndsWithEof()
        assertEquals(
            expected = Token(type = NUMBER, lexeme = "0.0", literal = 0.0, lineNumber = 1),
            actual = tokens.first()
        )
    }
}
