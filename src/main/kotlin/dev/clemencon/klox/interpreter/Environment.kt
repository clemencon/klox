package dev.clemencon.klox.interpreter

import dev.clemencon.klox.scanner.Token

/** Stores variable bindings for a single scope. Supports nil values. */
class Environment(val enclosing: Environment? = null) {
    private val values = mutableMapOf<String, Any?>()

    /** Defines a variable. Allows redefinition in the same scope. */
    fun define(name: String, value: Any?) {
        values[name] = value
    }

    /** Retrieves a variable. Throws RuntimeError with token location if undefined. */
    fun get(name: Token): Any? = when {
        name.lexeme in values -> values[name.lexeme]
        enclosing != null -> enclosing.get(name)
        else -> throw RuntimeError(name, "Undefined variable '${name.lexeme}'.")
    }

    /** Assigns to an existing variable. Throws RuntimeError if undefined. */
    fun assign(name: Token, value: Any?) {
        when {
            name.lexeme in values -> values[name.lexeme] = value
            enclosing != null -> enclosing.assign(name, value)
            else -> throw RuntimeError(name, "Undefined variable '${name.lexeme}'.")
        }
    }
}