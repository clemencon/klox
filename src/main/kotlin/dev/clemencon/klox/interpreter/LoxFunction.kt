package dev.clemencon.klox.interpreter

import dev.clemencon.klox.parser.FunctionStatement

class LoxFunction(val declaration: FunctionStatement) : LoxCallable {
    override val arity: Int get() = declaration.parameters.size

    override fun call(interpreter: Interpreter, arguments: List<Any?>): Any? {
        val environment = Environment(enclosing = interpreter.globals)

        declaration.parameters.zip(arguments).forEach { (parameter, argument) ->
            environment.define(name = parameter.lexeme, value = argument)
        }
        interpreter.executeBlock(declaration.body, environment)
        return null
    }

    override fun toString() = "<fn ${declaration.name.lexeme}>"
}