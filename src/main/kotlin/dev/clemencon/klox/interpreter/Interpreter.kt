package dev.clemencon.klox.interpreter

import dev.clemencon.klox.parser.*
import dev.clemencon.klox.scanner.TokenType.*

// ju: Extract each evaluation to a function?
fun Expression.evaluate(): Any? = when (this) {
    is Binary -> {
        val leftOperand = this.left.evaluate()
        val rightOperand = this.right.evaluate()

        when (this.operator.type) {
            MINUS -> leftOperand as Double - rightOperand as Double
            PLUS -> when {
                leftOperand is Double && rightOperand is Double -> leftOperand + rightOperand
                leftOperand is String && rightOperand is String -> leftOperand + rightOperand
                else -> throw Error() // ju: throw RuntimeError instead.
            }
            SLASH -> leftOperand as Double / rightOperand as Double
            STAR -> leftOperand as Double * rightOperand as Double
            else -> throw Error() // ju: For now? Or runtime error? It should be unreachable.
        }
    }

    is Grouping -> this.expression.evaluate()
    is Literal -> this.value
    is Unary -> {
        val operand = this.right.evaluate()
        when (this.operator.type) {
            MINUS -> -(operand as Double)
            BANG -> !isTruthy(operand)
            else -> throw Error() // ju: For now? Or runtime error? It should be unreachable.
        }
    }
}

private fun isTruthy(value: Any?) = when (value) {
    null -> false
    is Boolean -> value
    else -> true
}