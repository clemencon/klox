package dev.clemencon.klox.scanner

import dev.clemencon.klox.scanner.TokenType.EOF
import kotlin.test.assertEquals

fun scan(sourceCode: String): List<Token> {
    val scanner = Scanner(sourceCode)
    return scanner.scanTokens()
}

fun List<Token>.assertEndsWithEof() {
    val lastToken = last()
    assertEquals(EOF, lastToken.type)
    assertEquals("", lastToken.lexeme)
    assertEquals(null, lastToken.literal)
}

fun List<Token>.assertAmount(elements: Int) {
    assertEquals(elements, size)
}
