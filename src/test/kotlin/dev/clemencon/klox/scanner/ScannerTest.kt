package dev.clemencon.klox.scanner

import dev.clemencon.klox.Lox
import dev.clemencon.klox.scanner.TokenType.*
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals

class ScannerTest {

    @BeforeEach
    fun setUp() {
        // Reset error flag before each test
        Lox.hadError = false
    }

    // Combined/Realistic Scenarios
    @Test
    fun `variable declaration produces correct token sequence`() {
        val tokens = scan(sourceCode = "var x = 10;")

        tokens.assertAmount(6) // VAR, IDENTIFIER, EQUAL, NUMBER, SEMICOLON, EOF
        assertEquals(Token(type = VAR, lexeme = "var", literal = null, lineNumber = 1), tokens[0])
        assertEquals(Token(type = IDENTIFIER, lexeme = "x", literal = null, lineNumber = 1), tokens[1])
        assertEquals(Token(type = EQUAL, lexeme = "=", literal = null, lineNumber = 1), tokens[2])
        assertEquals(Token(type = NUMBER, lexeme = "10", literal = 10.0, lineNumber = 1), tokens[3])
        assertEquals(Token(type = SEMICOLON, lexeme = ";", literal = null, lineNumber = 1), tokens[4])
        tokens.assertEndsWithEof()
    }

    @Test
    fun `arithmetic expression produces correct token sequence`() {
        val tokens = scan(sourceCode = "1 + 2 * 3 - 4 / 5")

        tokens.assertAmount(10) // NUMBER, PLUS, NUMBER, STAR, NUMBER, MINUS, NUMBER, SLASH, NUMBER, EOF
        assertEquals(Token(type = NUMBER, lexeme = "1", literal = 1.0, lineNumber = 1), tokens[0])
        assertEquals(Token(type = PLUS, lexeme = "+", literal = null, lineNumber = 1), tokens[1])
        assertEquals(Token(type = NUMBER, lexeme = "2", literal = 2.0, lineNumber = 1), tokens[2])
        assertEquals(Token(type = STAR, lexeme = "*", literal = null, lineNumber = 1), tokens[3])
        assertEquals(Token(type = NUMBER, lexeme = "3", literal = 3.0, lineNumber = 1), tokens[4])
        assertEquals(Token(type = MINUS, lexeme = "-", literal = null, lineNumber = 1), tokens[5])
        assertEquals(Token(type = NUMBER, lexeme = "4", literal = 4.0, lineNumber = 1), tokens[6])
        assertEquals(Token(type = SLASH, lexeme = "/", literal = null, lineNumber = 1), tokens[7])
        assertEquals(Token(type = NUMBER, lexeme = "5", literal = 5.0, lineNumber = 1), tokens[8])
        tokens.assertEndsWithEof()
    }

    @Test
    fun `if statement with print produces correct token sequence`() {
        val tokens = scan(sourceCode = "if (x > 10) print \"big\";")

        tokens.assertAmount(10) // IF, LEFT_PAREN, IDENTIFIER, GREATER, NUMBER, RIGHT_PAREN, PRINT, STRING, SEMICOLON, EOF
        assertEquals(Token(type = IF, lexeme = "if", literal = null, lineNumber = 1), tokens[0])
        assertEquals(Token(type = LEFT_PAREN, lexeme = "(", literal = null, lineNumber = 1), tokens[1])
        assertEquals(Token(type = IDENTIFIER, lexeme = "x", literal = null, lineNumber = 1), tokens[2])
        assertEquals(Token(type = GREATER, lexeme = ">", literal = null, lineNumber = 1), tokens[3])
        assertEquals(Token(type = NUMBER, lexeme = "10", literal = 10.0, lineNumber = 1), tokens[4])
        assertEquals(Token(type = RIGHT_PAREN, lexeme = ")", literal = null, lineNumber = 1), tokens[5])
        assertEquals(Token(type = PRINT, lexeme = "print", literal = null, lineNumber = 1), tokens[6])
        assertEquals(Token(type = STRING, lexeme = "\"big\"", literal = "big", lineNumber = 1), tokens[7])
        assertEquals(Token(type = SEMICOLON, lexeme = ";", literal = null, lineNumber = 1), tokens[8])
        tokens.assertEndsWithEof()
    }

    @Test
    fun `multiple statements across multiple lines produce correct tokens`() {
        val tokens = scan(sourceCode = """
            var x = 1;
            var y = 2;
            print x + y;
        """.trimIndent())

        tokens.assertAmount(16) // 15 tokens + EOF
        // Line 1: var x = 1;
        assertEquals(1, tokens[0].lineNumber) // VAR
        assertEquals(1, tokens[1].lineNumber) // IDENTIFIER x
        assertEquals(1, tokens[2].lineNumber) // EQUAL
        assertEquals(1, tokens[3].lineNumber) // NUMBER 1
        assertEquals(1, tokens[4].lineNumber) // SEMICOLON
        // Line 2: var y = 2;
        assertEquals(2, tokens[5].lineNumber) // VAR
        assertEquals(2, tokens[6].lineNumber) // IDENTIFIER y
        assertEquals(2, tokens[7].lineNumber) // EQUAL
        assertEquals(2, tokens[8].lineNumber) // NUMBER 2
        assertEquals(2, tokens[9].lineNumber) // SEMICOLON
        // Line 3: print x + y;
        assertEquals(3, tokens[10].lineNumber) // PRINT
        assertEquals(3, tokens[11].lineNumber) // IDENTIFIER x
        assertEquals(3, tokens[12].lineNumber) // PLUS
        assertEquals(3, tokens[13].lineNumber) // IDENTIFIER y
        assertEquals(3, tokens[14].lineNumber) // SEMICOLON
        tokens.assertEndsWithEof()
    }

    @Test
    fun `empty lines between statements produce no spurious tokens`() {
        val tokens = scan(sourceCode = """
            var x = 1;


            var y = 2;
        """.trimIndent())

        tokens.assertAmount(11) // VAR, ID, EQ, NUM, SEMI, VAR, ID, EQ, NUM, SEMI, EOF
        assertEquals(1, tokens[0].lineNumber) // VAR on line 1
        assertEquals(4, tokens[5].lineNumber) // Second VAR on line 4 (after empty lines 2 and 3)
    }

    // Edge Cases
    @Test
    fun `very long identifier produces IDENTIFIER token`() {
        val longName = "a".repeat(1000)
        val tokens = scan(sourceCode = longName)

        tokens.assertAmount(2)
        tokens.assertEndsWithEof()
        assertEquals(
            expected = Token(type = IDENTIFIER, lexeme = longName, literal = null, lineNumber = 1),
            actual = tokens.first()
        )
    }

    @Test
    fun `very large number produces NUMBER token`() {
        val largeNumber = "999999999999999999.123456789"
        val tokens = scan(sourceCode = largeNumber)

        tokens.assertAmount(2)
        tokens.assertEndsWithEof()
        assertEquals(
            expected = Token(type = NUMBER, lexeme = largeNumber, literal = 999999999999999999.123456789, lineNumber = 1),
            actual = tokens.first()
        )
    }

    @Test
    fun `string containing all special characters produces STRING token`() {
        // This tests that strings can contain characters that would otherwise be operators/keywords
        val tokens = scan(sourceCode = "\"var class if while = == != < > <= >= + - * / ( ) { } , . ; and or\"")

        tokens.assertAmount(2)
        tokens.assertEndsWithEof()
        assertEquals(
            expected = Token(
                type = STRING,
                lexeme = "\"var class if while = == != < > <= >= + - * / ( ) { } , . ; and or\"",
                literal = "var class if while = == != < > <= >= + - * / ( ) { } , . ; and or",
                lineNumber = 1
            ),
            actual = tokens.first()
        )
    }


    @Test
    fun `number followed by identifier produces two tokens`() {
        val tokens = scan(sourceCode = "123abc")

        tokens.assertAmount(3) // NUMBER, IDENTIFIER, EOF
        assertEquals(
            expected = Token(type = NUMBER, lexeme = "123", literal = 123.0, lineNumber = 1),
            actual = tokens[0]
        )
        assertEquals(
            expected = Token(type = IDENTIFIER, lexeme = "abc", literal = null, lineNumber = 1),
            actual = tokens[1]
        )
        tokens.assertEndsWithEof()
    }

    @Test
    fun `multiple dots produce number dot number sequence`() {
        val tokens = scan(sourceCode = "1.2.3")

        tokens.assertAmount(4) // NUMBER(1.2), DOT, NUMBER(3), EOF
        assertEquals(
            expected = Token(type = NUMBER, lexeme = "1.2", literal = 1.2, lineNumber = 1),
            actual = tokens[0]
        )
        assertEquals(
            expected = Token(type = DOT, lexeme = ".", literal = null, lineNumber = 1),
            actual = tokens[1]
        )
        assertEquals(
            expected = Token(type = NUMBER, lexeme = "3", literal = 3.0, lineNumber = 1),
            actual = tokens[2]
        )
        tokens.assertEndsWithEof()
    }
}
