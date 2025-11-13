package dev.clemencon.klox.interpreter

import dev.clemencon.klox.scanner.Token

class RuntimeError(
    val token: Token,
    message: String
) : RuntimeException(message)