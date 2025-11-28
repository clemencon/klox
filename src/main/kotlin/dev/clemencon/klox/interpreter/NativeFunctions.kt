package dev.clemencon.klox.interpreter

/** Native functions available in global scope. */
object NativeFunctions {

    /** Returns Unix time in seconds since epoch. */
    val clock = object : LoxCallable {
        override val arity = 0

        override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
            return System.currentTimeMillis() / 1000.0
        }

        override fun toString(): String = "native fn"
    }
}