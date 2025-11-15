package dev.clemencon.klox

import dev.clemencon.klox.ExitStatus.DATAERR
import dev.clemencon.klox.ExitStatus.EX_SOFTWARE
import dev.clemencon.klox.interpreter.Interpreter
import dev.clemencon.klox.interpreter.RuntimeError
import dev.clemencon.klox.parser.Parser
import dev.clemencon.klox.scanner.Scanner
import dev.clemencon.klox.scanner.Token
import dev.clemencon.klox.scanner.TokenType.EOF
import java.io.File
import kotlin.system.exitProcess

/** Orchestrates the interpreter pipeline and centralizes error reporting. */
class Lox {
    companion object {
        /** Reused across REPL runs to preserve state. */
        private val interpreter = Interpreter()

        /** Tracks scan/parse errors. Prevents execution of malformed code; reset after each REPL line. */
        var hadError = false

        /** Tracks runtime errors. Causes exit code 70 in file mode. */
        var hadRuntimeError = false

        /** Reports scan errors without token context (unexpected characters, unterminated strings). */
        fun error(line: Int, message: String) = report(line, "", message)

        /** Reports runtime errors with token location for context. */
        fun runtimeError(error: RuntimeError) {
            System.err.println("${error.message}\n[line ${error.token.lineNumber}]")
            hadRuntimeError = true
        }

        /** Centralizes error formatting. Scanner and parser detect; this reports. */
        private fun report(line: Int, where: String, message: String) {
            System.err.println("[line $line] Error$where: $message")
            hadError = true
        }

        /** Reports parse errors. Shows lexeme for context, except EOF uses 'at end'. */
        fun error(token: Token, message: String) = when (token.type) {
            EOF -> report(token.lineNumber, " at end", message)
            else -> report(token.lineNumber, " at '${token.lexeme}'", message)
        }
    }

    /** Runs a Lox file. Exits with error code if scan/parse errors occur. */
    fun runFile(pathname: String) {
        val sourceCode = File(pathname).readText()
        run(sourceCode)
        if (hadError) exitProcess(DATAERR.code)
        if (hadRuntimeError) exitProcess(EX_SOFTWARE.code)
    }

    /** Interactive REPL. Errors don't terminate; hadError reset after each line. */
    fun runPrompt() {
        while (true) {
            print("> ")
            val line = readlnOrNull() ?: break
            run(line)
            hadError = false
        }
    }

    /** Interpreter pipeline: source → tokens → AST → evaluation. */
    private fun run(sourceCode: String) {
        val tokens: List<Token> = Scanner(sourceCode).scanTokens()
        val parser = Parser(tokens)
        val expression = parser.parse()

        // Stop if there was a syntax error.
        if (!hadError) interpreter.interpret(expression)
    }
}