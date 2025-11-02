package dev.clemencon.klox.scanner

import dev.clemencon.klox.Lox
import dev.clemencon.klox.scanner.TokenType.*
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals

class WhitespaceHandlingTest {

    @BeforeEach
    fun setUp() {
        // Reset error flag before each test
        Lox.hadError = false
    }

    @Test
    fun `whitespace between tokens is ignored`() {
        val tokens = scan(sourceCode = "+  \t  \t-")

        tokens.assertAmount(3) // PLUS, MINUS, EOF
        tokens.assertEndsWithEof()
        assertEquals(
            expected = Token(type = PLUS, lexeme = "+", literal = null, lineNumber = 1),
            actual = tokens[0]
        )
        assertEquals(
            expected = Token(type = MINUS, lexeme = "-", literal = null, lineNumber = 1),
            actual = tokens[1]
        )
    }

    @Test
    fun `carriage return newline sequence increments line once`() {
        val tokens = scan(sourceCode = "+\r\n-")

        tokens.assertAmount(3) // PLUS, MINUS, EOF
        // Both tokens should be on different lines (line 1 and line 2)
        assertEquals(1, tokens[0].lineNumber) // PLUS on line 1
        assertEquals(2, tokens[1].lineNumber) // MINUS on line 2
    }
}
