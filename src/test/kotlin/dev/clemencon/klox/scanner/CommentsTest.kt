package dev.clemencon.klox.scanner

import dev.clemencon.klox.Lox
import dev.clemencon.klox.scanner.TokenType.*
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals

class CommentsTest {

    @BeforeEach
    fun setUp() {
        // Reset error flag before each test
        Lox.hadError = false
    }

    @Test
    fun `double slash comment produces only EOF`() {
        val tokens = scan(sourceCode = "// this is a comment")

        tokens.assertAmount(1)
        tokens.assertEndsWithEof()
    }

    @Test
    fun `comment after token is ignored`() {
        val tokens = scan(sourceCode = "+ // this is a comment")

        tokens.assertAmount(2) // PLUS, EOF
        tokens.assertEndsWithEof()
        assertEquals(
            expected = Token(type = PLUS, lexeme = "+", literal = null, lineNumber = 1),
            actual = tokens.first()
        )
    }

    @Test
    fun `empty comment produces only EOF`() {
        val tokens = scan(sourceCode = "//")

        tokens.assertAmount(1)
        tokens.assertEndsWithEof()
    }

    @Test
    fun `comment followed by newline and token produces token on line two`() {
        val tokens = scan(sourceCode = "// comment\n+")

        tokens.assertAmount(2) // PLUS, EOF
        tokens.assertEndsWithEof()
        assertEquals(
            expected = Token(type = PLUS, lexeme = "+", literal = null, lineNumber = 2),
            actual = tokens.first()
        )
    }

    @Test
    fun `comment at end of file without newline works correctly`() {
        val tokens = scan(sourceCode = "+ // comment at end")

        tokens.assertAmount(2) // PLUS, EOF
        tokens.assertEndsWithEof()
        assertEquals(
            expected = Token(type = PLUS, lexeme = "+", literal = null, lineNumber = 1),
            actual = tokens.first()
        )
    }
}
