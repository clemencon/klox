package dev.clemencon.klox.interpreter

import dev.clemencon.klox.scanner.Token

/** Stores variable bindings for a single scope. Supports nil values. */
class Environment {
    private val values = mutableMapOf<String, Any?>()

    /** Defines a variable. Allows redefinition in the same scope. */
    fun define(name: String, value: Any?) = values.put(name, value)

    /** Retrieves a variable. Throws RuntimeError with token location if undefined. */
    fun get(name: Token): Any? =
        values.getOrElse(name.lexeme) { throw RuntimeError(name, "Undefined variable '${name.lexeme}'.") }
}