package dev.clemencon.klox.scanner

import dev.clemencon.klox.Lox
import dev.clemencon.klox.scanner.TokenType.*
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals

class ErrorHandlingTest {

    @BeforeEach
    fun setUp() {
        // Reset error flag before each test
        Lox.hadError = false
    }

    @Test
    fun `unexpected character reports error but continues scanning`() {
        val tokens = scan(sourceCode = "+ @ -")

        // Scanner should report error for '@' but continue scanning
        // Should produce: PLUS, MINUS, EOF (the '@' is skipped)
        tokens.assertAmount(3)
        assertEquals(
            expected = Token(type = PLUS, lexeme = "+", literal = null, lineNumber = 1),
            actual = tokens[0]
        )
        assertEquals(
            expected = Token(type = MINUS, lexeme = "-", literal = null, lineNumber = 1),
            actual = tokens[1]
        )
        tokens.assertEndsWithEof()
    }

    @Test
    fun `multiple errors in source reports all errors`() {
        val tokens = scan(sourceCode = "+ @ - # *")

        // Scanner should report errors for '@' and '#' but continue scanning
        // Should produce: PLUS, MINUS, STAR, EOF (the '@' and '#' are skipped)
        tokens.assertAmount(4)
        assertEquals(
            expected = Token(type = PLUS, lexeme = "+", literal = null, lineNumber = 1),
            actual = tokens[0]
        )
        assertEquals(
            expected = Token(type = MINUS, lexeme = "-", literal = null, lineNumber = 1),
            actual = tokens[1]
        )
        assertEquals(
            expected = Token(type = STAR, lexeme = "*", literal = null, lineNumber = 1),
            actual = tokens[2]
        )
        tokens.assertEndsWithEof()
    }
}
