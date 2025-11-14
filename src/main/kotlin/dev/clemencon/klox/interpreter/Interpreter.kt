package dev.clemencon.klox.interpreter

import dev.clemencon.klox.Lox
import dev.clemencon.klox.parser.*
import dev.clemencon.klox.scanner.Token
import dev.clemencon.klox.scanner.TokenType.*

class Interpreter {
    fun interpret(expression: Expression) {
        try {
            val value = expression.evaluate()
            println(stringify(value))
        } catch (error: RuntimeError) {
            Lox.runtimeError(error)
        }
    }
}

private fun Expression.evaluate(): Any? = when (this) {
    is Binary -> evaluate(this)
    is Grouping -> evaluate(this)
    is Literal -> evaluate(this)
    is Unary -> evaluate(this)
}

private fun evaluate(binary: Binary): Any {
    val left = binary.left.evaluate()
    val right = binary.right.evaluate()

    return when (binary.operator.type) {
        BANG_EQUAL -> left != right
        EQUAL_EQUAL -> left == right
        GREATER -> {
            val (left, right) = requireNumberOperands(binary.operator, left, right)
            left > right
        }

        GREATER_EQUAL -> {
            val (left, right) = requireNumberOperands(binary.operator, left, right)
            left >= right
        }

        LESS -> {
            val (left, right) = requireNumberOperands(binary.operator, left, right)
            left < right
        }

        LESS_EQUAL -> {
            val (left, right) = requireNumberOperands(binary.operator, left, right)
            left <= right
        }

        SLASH -> {
            val (left, right) = requireNumberOperands(binary.operator, left, right)
            left / right
        }

        STAR -> {
            val (left, right) = requireNumberOperands(binary.operator, left, right)
            left * right
        }

        MINUS -> {
            val (left, right) = requireNumberOperands(binary.operator, left, right)
            left - right
        }

        PLUS -> when {
            left is Double && right is Double -> left + right
            left is String && right is String -> left + right
            else -> throw RuntimeError(binary.operator, "Operands must be two numbers or two strings.")
        }

        else -> error("Unreachable: Unknown binary operator ${binary.operator.type}")
    }
}

private fun evaluate(grouping: Grouping): Any? {
    return grouping.expression.evaluate()
}

private fun evaluate(literal: Literal): Any? {
    return literal.value
}

private fun evaluate(unary: Unary): Any {
    val right = unary.right.evaluate()

    return when (unary.operator.type) {
        MINUS -> -requireNumberOperand(unary.operator, right)
        BANG -> !isTruthy(right)
        else -> error("Unreachable: Unknown unary operator ${unary.operator.type}")
    }
}

private fun isTruthy(value: Any?) = when (value) {
    null -> false
    is Boolean -> value
    else -> true
}

private fun stringify(value: Any?) = when (value) {
    null -> "nil"
    is Double -> value.toString().removeSuffix(".0")
    else -> value.toString()
}

private fun requireNumberOperand(operator: Token, right: Any?): Double {
    if (right !is Double) {
        throw RuntimeError(operator, "Operand must be a number.")
    }
    return right
}

private fun requireNumberOperands(operator: Token, left: Any?, right: Any?): Pair<Double, Double> {
    if (left !is Double || right !is Double) {
        throw RuntimeError(operator, "Operands must be numbers.")
    }
    return left to right
}