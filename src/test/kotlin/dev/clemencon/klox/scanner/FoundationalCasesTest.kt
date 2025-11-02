package dev.clemencon.klox.scanner

import dev.clemencon.klox.Lox
import dev.clemencon.klox.scanner.TokenType.*
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals

class FoundationalCasesTest {

    @BeforeEach
    fun setUp() {
        // Reset error flag before each test
        Lox.hadError = false
    }

    @Test
    fun `empty source code produces only EOF token`() {
        val tokens = scan(sourceCode = "")

        tokens.assertAmount(1)
        tokens.assertEndsWithEof()
    }

    @Test
    fun `single whitespace character produces only EOF token`() {
        val tokens = scan(sourceCode = " ")

        tokens.assertAmount(1)
        tokens.assertEndsWithEof()
    }

    @Test
    fun `single newline produces only EOF token`() {
        val tokens = scan(sourceCode = "\n")

        tokens.assertAmount(1)
        tokens.assertEndsWithEof()
    }
}
