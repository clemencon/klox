package dev.clemencon.klox

import dev.clemencon.klox.scanner.Scanner
import dev.clemencon.klox.scanner.Token
import dev.clemencon.klox.ExitStatus.*
import dev.clemencon.klox.scanner.TokenType.*
import java.io.File
import kotlin.collections.forEach
import kotlin.system.exitProcess

/**
 * The Lox interpreter. Supports file execution and REPL mode.
 */
class Lox {
    // ju: Use a top-level property instead?
    companion object {
        var hadError = false

        fun error(line: Int, message: String) {
            report(line, "", message)
        }

        private fun report(line: Int, where: String, message: String) {
            System.err.println("[line $line] Error$where: $message")
            hadError = true
        }

        fun error(token: Token, message: String) {
            if (token.type == EOF) {
                report(token.lineNumber, " at end", message)
            } else {
                report(token.lineNumber, " at '${token.lexeme}'", message)
            }
        }
    }

    fun runFile(pathname: String) {
        val sourceCode = File(pathname).readText()
        run(sourceCode)
        if (hadError) exitProcess(DATAERR.code)
    }

    /**
     * Run an interactive Read-Eval-Print Loop.
     */
    fun runPrompt() {
        while (true) {
            print("> ")
            val line = readlnOrNull() ?: break
            run(line)
            hadError = false
        }
    }

    private fun run(sourceCode: String) {
        val tokens: List<Token> = Scanner(sourceCode).scanTokens()
        // Temporary: print tokens for debugging.
        tokens.forEach { println(it) }
    }
}