package dev.clemencon.klox

import dev.clemencon.klox.scanner.Scanner
import dev.clemencon.klox.scanner.Token
import dev.clemencon.klox.ExitStatus.*
import dev.clemencon.klox.parser.Parser
import dev.clemencon.klox.scanner.TokenType.*
import java.io.File
import kotlin.system.exitProcess

/**
 * The main Lox interpreter class.
 * Orchestrates the interpreter pipeline and centralizes error reporting.
 * Supports file and REPL execution modes.
 */
class Lox {
    companion object {
        /**
         * Tracks whether errors occurred during scanning or parsing.
         * Prevents execution of malformed code. In file mode, errors cause immediate exit.
         * In REPL mode, reset after each line to allow recovery without restarting.
         */
        var hadError = false

        /**
         * Reports an error at a line number.
         * Used by the scanner for errors without associated tokens (unexpected characters, unterminated strings).
         */
        fun error(line: Int, message: String) {
            report(line, "", message)
        }

        /**
         * Core error reporting function.
         * Centralizes error presentation so scanner and parser only detect problems.
         * Error formatting can be changed here without modifying detection logic.
         */
        private fun report(line: Int, where: String, message: String) {
            System.err.println("[line $line] Error$where: $message")
            hadError = true
        }

        /**
         * Reports an error associated with a token.
         * Used by the parser for token-specific errors.
         * Shows the problematic lexeme for context, except for EOF where "at end" is clearer.
         */
        fun error(token: Token, message: String) {
            if (token.type == EOF) {
                report(token.lineNumber, " at end", message)
            } else {
                report(token.lineNumber, " at '${token.lexeme}'", message)
            }
        }
    }

    /**
     * Executes a Lox source file.
     * Exits with DATAERR code if errors are detected during scanning or parsing.
     * This prevents running malformed programs.
     */
    fun runFile(pathname: String) {
        val sourceCode = File(pathname).readText()
        run(sourceCode)
        if (hadError) exitProcess(DATAERR.code)
    }

    /**
     * Runs an interactive REPL.
     * Unlike file mode, errors don't terminate the session.
     * The error flag is reset after each line to allow experimentation and recovery without restarting.
     * Exits on EOF (Ctrl+D on Unix).
     */
    fun runPrompt() {
        while (true) {
            print("> ")
            val line = readlnOrNull() ?: break
            run(line)
            hadError = false
        }
    }

    /**
     * Executes the interpreter pipeline on source code.
     * Orchestrates scanning (source → tokens) and parsing (tokens → AST).
     * Currently, prints the parsed expression for verification; evaluation phase will be added later.
     */
    private fun run(sourceCode: String) {
        val tokens: List<Token> = Scanner(sourceCode).scanTokens()
        val parser = Parser(tokens)
        val expression = parser.parse();

        if (hadError) return

        println(expression)
    }
}