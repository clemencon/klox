package dev.clemencon.klox.scanner

import dev.clemencon.klox.Lox
import dev.clemencon.klox.scanner.TokenType.*
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals

class LineNumberTrackingTest {

    @BeforeEach
    fun setUp() {
        // Reset error flag before each test
        Lox.hadError = false
    }

    @Test
    fun `all tokens on single line report line one`() {
        val tokens = scan(sourceCode = "+ - * /")

        tokens.assertAmount(5) // PLUS, MINUS, STAR, SLASH, EOF
        tokens.forEach { token ->
            assertEquals(1, token.lineNumber)
        }
    }

    @Test
    fun `tokens on different lines report correct line numbers`() {
        val tokens = scan(sourceCode = "+\n-\n*\n/")

        tokens.assertAmount(5) // PLUS, MINUS, STAR, SLASH, EOF
        assertEquals(1, tokens[0].lineNumber) // PLUS
        assertEquals(2, tokens[1].lineNumber) // MINUS
        assertEquals(3, tokens[2].lineNumber) // STAR
        assertEquals(4, tokens[3].lineNumber) // SLASH
    }

    @Test
    fun `string with embedded newline increments line number`() {
        val tokens = scan(sourceCode = "\"line1\nline2\"\n+")

        tokens.assertAmount(3) // STRING, PLUS, EOF
        // String ends on line 2
        assertEquals(2, tokens[0].lineNumber)
        // PLUS is on line 3
        assertEquals(3, tokens[1].lineNumber)
    }

    @Test
    fun `multiple consecutive newlines track line numbers correctly`() {
        val tokens = scan(sourceCode = "+\n\n\n-")

        tokens.assertAmount(3) // PLUS, MINUS, EOF
        assertEquals(1, tokens[0].lineNumber) // PLUS
        assertEquals(4, tokens[1].lineNumber) // MINUS (after 3 newlines)
    }
}
