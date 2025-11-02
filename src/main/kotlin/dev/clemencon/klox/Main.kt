package dev.clemencon.klox

import dev.clemencon.klox.ExitStatus.*
import kotlin.system.exitProcess

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
