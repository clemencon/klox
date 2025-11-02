package dev.clemencon.klox.scanner

import dev.clemencon.klox.Lox
import dev.clemencon.klox.scanner.TokenType.*
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals

class IdentifiersTest {

    @BeforeEach
    fun setUp() {
        // Reset error flag before each test
        Lox.hadError = false
    }

    @Test
    fun `alphabetic text produces IDENTIFIER token`() {
        val tokens = scan(sourceCode = "myVariable")

        tokens.assertAmount(2)
        tokens.assertEndsWithEof()
        assertEquals(
            expected = Token(type = IDENTIFIER, lexeme = "myVariable", literal = null, lineNumber = 1),
            actual = tokens.first()
        )
    }

    @Test
    fun `identifier with letters and digits produces IDENTIFIER token`() {
        val tokens = scan(sourceCode = "var123")

        tokens.assertAmount(2)
        tokens.assertEndsWithEof()
        assertEquals(
            expected = Token(type = IDENTIFIER, lexeme = "var123", literal = null, lineNumber = 1),
            actual = tokens.first()
        )
    }

    @Test
    fun `capitalized word produces IDENTIFIER not keyword`() {
        val tokens = scan(sourceCode = "MyClass")

        tokens.assertAmount(2)
        tokens.assertEndsWithEof()
        assertEquals(
            expected = Token(type = IDENTIFIER, lexeme = "MyClass", literal = null, lineNumber = 1),
            actual = tokens.first()
        )
    }

    @Test
    fun `word starting with keyword text produces IDENTIFIER`() {
        val tokens = scan(sourceCode = "forLoop")

        tokens.assertAmount(2)
        tokens.assertEndsWithEof()
        assertEquals(
            expected = Token(type = IDENTIFIER, lexeme = "forLoop", literal = null, lineNumber = 1),
            actual = tokens.first()
        )
    }

    @Test
    fun `word containing keyword text produces IDENTIFIER`() {
        val tokens = scan(sourceCode = "isClassified")

        tokens.assertAmount(2)
        tokens.assertEndsWithEof()
        assertEquals(
            expected = Token(type = IDENTIFIER, lexeme = "isClassified", literal = null, lineNumber = 1),
            actual = tokens.first()
        )
    }

    @Test
    fun `single letter produces IDENTIFIER token`() {
        val tokens = scan(sourceCode = "x")

        tokens.assertAmount(2)
        tokens.assertEndsWithEof()
        assertEquals(
            expected = Token(type = IDENTIFIER, lexeme = "x", literal = null, lineNumber = 1),
            actual = tokens.first()
        )
    }

    @Test
    fun `underscore alone produces IDENTIFIER token`() {
        val tokens = scan(sourceCode = "_")

        tokens.assertAmount(2)
        tokens.assertEndsWithEof()
        assertEquals(
            expected = Token(type = IDENTIFIER, lexeme = "_", literal = null, lineNumber = 1),
            actual = tokens.first()
        )
    }

    @Test
    fun `identifier starting with underscore produces IDENTIFIER token`() {
        val tokens = scan(sourceCode = "_private")

        tokens.assertAmount(2)
        tokens.assertEndsWithEof()
        assertEquals(
            expected = Token(type = IDENTIFIER, lexeme = "_private", literal = null, lineNumber = 1),
            actual = tokens.first()
        )
    }

    @Test
    fun `identifier with underscores produces IDENTIFIER token`() {
        val tokens = scan(sourceCode = "my_variable_name")

        tokens.assertAmount(2)
        tokens.assertEndsWithEof()
        assertEquals(
            expected = Token(type = IDENTIFIER, lexeme = "my_variable_name", literal = null, lineNumber = 1),
            actual = tokens.first()
        )
    }

    @Test
    fun `identifier ending with underscore produces IDENTIFIER token`() {
        val tokens = scan(sourceCode = "value_")

        tokens.assertAmount(2)
        tokens.assertEndsWithEof()
        assertEquals(
            expected = Token(type = IDENTIFIER, lexeme = "value_", literal = null, lineNumber = 1),
            actual = tokens.first()
        )
    }
}
