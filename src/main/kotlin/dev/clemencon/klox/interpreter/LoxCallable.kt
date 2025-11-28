package dev.clemencon.klox.interpreter

/** Values that can be called like functions (user functions, native functions, classes). */
interface LoxCallable {
    /** Number of parameters expected. Checked before call() to report arity errors. */
    val arity: Int

    /** Invokes the callable with arguments. Interpreter provides access to environment. */
    fun call(interpreter: Interpreter, arguments: List<Any?>): Any?
}