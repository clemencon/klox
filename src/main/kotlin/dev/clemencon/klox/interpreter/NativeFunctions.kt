package dev.clemencon.klox.interpreter

object NativeFunctions {
    val clock = object : LoxCallable {
        override val arity = 0

        override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
            return System.currentTimeMillis() / 1000.0
        }

        override fun toString(): String = "native fn"
    }
}