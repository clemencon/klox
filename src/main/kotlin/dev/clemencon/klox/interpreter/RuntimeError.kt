package dev.clemencon.klox.interpreter

import dev.clemencon.klox.scanner.Token

/** Runtime error with token for location-aware error reporting. */
class RuntimeError(
    val token: Token,
    message: String
) : RuntimeException(message)