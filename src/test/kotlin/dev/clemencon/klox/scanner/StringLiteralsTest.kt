package dev.clemencon.klox.scanner

import dev.clemencon.klox.Lox
import dev.clemencon.klox.scanner.TokenType.*
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals

class StringLiteralsTest {

    @BeforeEach
    fun setUp() {
        // Reset error flag before each test
        Lox.hadError = false
    }

    @Test
    fun `quoted text produces STRING token with literal value`() {
        val tokens = scan(sourceCode = "\"hello world\"")

        tokens.assertAmount(2)
        tokens.assertEndsWithEof()
        assertEquals(
            expected = Token(type = STRING, lexeme = "\"hello world\"", literal = "hello world", lineNumber = 1),
            actual = tokens.first()
        )
    }

    @Test
    fun `empty quoted string produces STRING token with empty literal`() {
        val tokens = scan(sourceCode = "\"\"")

        tokens.assertAmount(2)
        tokens.assertEndsWithEof()
        assertEquals(
            expected = Token(type = STRING, lexeme = "\"\"", literal = "", lineNumber = 1),
            actual = tokens.first()
        )
    }

    @Test
    fun `multiline string produces STRING token and tracks line numbers correctly`() {
        val tokens = scan(sourceCode = "\"line 1\nline 2\nline 3\"\n+")

        tokens.assertAmount(3) // STRING, PLUS, EOF
        // The STRING token's line number reflects the line where the string ends (line 3)
        assertEquals(
            expected = Token(
                type = STRING,
                lexeme = "\"line 1\nline 2\nline 3\"",
                literal = "line 1\nline 2\nline 3",
                lineNumber = 3
            ),
            actual = tokens[0]
        )
        // The token after the multiline string should be on line 4
        assertEquals(
            expected = Token(type = PLUS, lexeme = "+", literal = null, lineNumber = 4),
            actual = tokens[1]
        )
    }

    @Test
    fun `unterminated string reports an error`() {
        val tokens = scan(sourceCode = "\"unterminated")

        // Should only produce EOF, no STRING token
        tokens.assertAmount(1)
        tokens.assertEndsWithEof()
    }

    @Test
    fun `string with special characters produces STRING token`() {
        val tokens = scan(sourceCode = "\"!@#$%^&*()_+-=[]{}|;:',.<>?/~`\"")

        tokens.assertAmount(2)
        tokens.assertEndsWithEof()
        assertEquals(
            expected = Token(
                type = STRING,
                lexeme = "\"!@#$%^&*()_+-=[]{}|;:',.<>?/~`\"",
                literal = "!@#$%^&*()_+-=[]{}|;:',.<>?/~`",
                lineNumber = 1
            ),
            actual = tokens.first()
        )
    }
}
