package dev.clemencon.klox

import dev.clemencon.klox.ExitStatus.OK
import dev.clemencon.klox.ExitStatus.USAGE
import kotlin.system.exitProcess

/** Entry point. Runs REPL with no args, executes file with one arg. */
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
