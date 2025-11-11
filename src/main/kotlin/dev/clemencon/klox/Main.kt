package dev.clemencon.klox

import dev.clemencon.klox.ExitStatus.*
import kotlin.system.exitProcess

/**
 * Entry point for the Klox interpreter.
 * Dispatches to the appropriate execution mode:
 * - No arguments: interactive REPL
 * - One argument: execute Lox source file
 * - Multiple arguments: print usage and exit with error
 */
fun main(args: Array<String>) {
    when (args.size) {
        0 -> Lox().runPrompt()
        1 -> Lox().runFile(pathname = args.first())
        else -> {
            println("Usage: klox [script]")
            exitProcess(USAGE.code)
        }
    }

    exitProcess(OK.code)
}
