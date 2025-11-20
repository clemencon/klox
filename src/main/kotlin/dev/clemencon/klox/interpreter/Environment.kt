package dev.clemencon.klox.interpreter

import dev.clemencon.klox.scanner.Token

/** Stores variable bindings for a single scope. Supports nil values. */
class Environment {
    private val values = mutableMapOf<String, Any?>()

    /** Defines a variable. Allows redefinition in the same scope. */
    fun define(name: String, value: Any?) {
        values[name] = value
    }

    /** Retrieves a variable. Throws RuntimeError with token location if undefined. */
    fun get(name: Token): Any? =
        values.getOrElse(name.lexeme) { throw RuntimeError(name, "Undefined variable '${name.lexeme}'.") }

    /** Assigns to an existing variable. Throws RuntimeError if undefined. */
    fun assign(name: Token, value: Any?) {
        if (name.lexeme !in values) throw RuntimeError(name, "Undefined variable '${name.lexeme}'.")
        values[name.lexeme] = value
    }
}