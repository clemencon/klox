package dev.clemencon.klox.interpreter

import dev.clemencon.klox.Lox
import dev.clemencon.klox.parser.*
import dev.clemencon.klox.scanner.Token
import dev.clemencon.klox.scanner.TokenType.*

/** Tree-walk interpreter. Executes statements and catches runtime errors. */
class Interpreter() {
    val globals = Environment()
    private var environment: Environment = globals

    init {
        globals.define("clock", NativeFunctions.clock)
    }

    fun interpret(statements: List<Statement>) {
        try {
            statements.forEach { it.execute() }
        } catch (error: RuntimeError) {
            Lox.runtimeError(error)
        }
    }

    private fun Statement.execute() = when (this) {
        is ExpressionStatement -> execute(this)
        is FunctionStatement -> execute(this)
        is IfStatement -> execute(this)
        is PrintStatement -> execute(this)
        is VariableDeclaration -> execute(this)
        is WhileStatement -> execute(this)
        is BlockStatement -> execute(this, Environment(enclosing = environment)) // Creates a child scope.
    }

    /** Evaluates expressions using polymorphic dispatch. */
    private fun Expression.evaluate(): Any? = when (this) {
        is Binary -> evaluate(this)
        is Call -> evaluate(this)
        is Grouping -> evaluate(this)
        is Literal -> evaluate(this)
        is Unary -> evaluate(this)
        is Variable -> evaluate(this)
        is Assignment -> evaluate(this)
        is Logical -> evaluate(this)
    }

    /** Expression statement: evaluates for side effects, discards result. */
    private fun execute(expressionStatement: ExpressionStatement) {
        expressionStatement.expression.evaluate()
    }

    private fun execute(functionStatement: FunctionStatement) {
        val function = LoxFunction(declaration = functionStatement)
        environment.define(name = functionStatement.name.lexeme, value = function)
    }

    private fun execute(ifStatement: IfStatement) {
        when (isTruthy(ifStatement.condition.evaluate())) {
            true -> ifStatement.thenBranch.execute()
            false -> ifStatement.elseBranch?.execute()
        }
    }

    private fun execute(printStatement: PrintStatement) = println(stringify(printStatement.expression.evaluate()))

    /** Variable declaration and initialization. Uninitialized variables default to nil. */
    private fun execute(variableDeclaration: VariableDeclaration) {
        val value = variableDeclaration.initializer?.evaluate()
        environment.define(variableDeclaration.name.lexeme, value)
    }

    private fun execute(whileStatement: WhileStatement) {
        while (isTruthy(whileStatement.condition.evaluate())) {
            whileStatement.body.execute()
        }
    }

    /** Executes block in new scope. Saves current environment, switches to block's, restores on exit. */
    private fun execute(blockStatement: BlockStatement, environment: Environment) {
        val previous = this.environment
        try {
            this.environment = environment
            blockStatement.statements.forEach { it.execute() }
        } finally {
            this.environment = previous
        }
    }

    /** Binary operators. Type-checks operands to throw RuntimeError (with token location). */
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

            // PLUS supports both number addition and string concatenation.
            PLUS -> when {
                (left is Double && right is Double) -> left + right
                (left is String && right is String) -> left + right
                else -> throw RuntimeError(binary.operator, "Operands must be two numbers or two strings.")
            }

            else -> error("Unreachable: Unknown binary operator ${binary.operator.type}")
        }
    }

    private fun evaluate(call: Call): Any? {
        val callee = call.callee.evaluate()
        val arguments = call.arguments.map { it.evaluate() }

        if (callee !is LoxCallable) {
            throw RuntimeError(call.closingParen, "Can only call functions and classes.")
        }
        if (arguments.size != callee.arity) {
            throw RuntimeError(call.closingParen, "Expected ${callee.arity} arguments but got ${arguments.size}.")
        }

        return callee.call(this, arguments)
    }

    private fun evaluate(grouping: Grouping): Any? = grouping.expression.evaluate()

    private fun evaluate(literal: Literal): Any? = literal.value

    private fun evaluate(logical: Logical): Any? {
        val left = logical.left.evaluate()

        return when (logical.operator.type) {
            OR -> if (isTruthy(left)) left else logical.right.evaluate()
            AND -> if (!isTruthy(left)) left else logical.right.evaluate()
            else -> error("Unreachable: Unknown logical operator ${logical.operator.type}")
        }
    }

    private fun evaluate(unary: Unary): Any {
        val right = unary.right.evaluate()

        return when (unary.operator.type) {
            MINUS -> -requireNumberOperand(unary.operator, right)
            BANG -> !isTruthy(right)
            else -> error("Unreachable: Unknown unary operator ${unary.operator.type}")
        }
    }

    /** Variable lookup. Throws RuntimeError if undefined. */
    private fun evaluate(variable: Variable): Any? = environment.get(variable.name)

    /** Assignment: evaluates value, assigns to variable, returns the value (enables a = b = 5). */
    private fun evaluate(assignment: Assignment): Any? {
        val value = assignment.value.evaluate()
        environment.assign(assignment.name, value)
        return value
    }
}

/** Lox truthiness: only false and nil are falsy, 0 and "" are truthy. */
private fun isTruthy(value: Any?) = when (value) {
    null -> false
    is Boolean -> value
    else -> true
}

/** Converts to Lox string representation. Strips ".0" suffix: 42.0 → "42". */
private fun stringify(value: Any?) = when (value) {
    null -> "nil"
    is Double -> value.toString().removeSuffix(".0")
    else -> value.toString()
}

/** Type-checks unary operand. Throws RuntimeError (with token location) instead of ClassCastException. */
private fun requireNumberOperand(operator: Token, right: Any?) = when {
    right is Double -> right
    else -> throw RuntimeError(operator, "Operand must be a number.")
}

/** Type-checks binary operands. Throws RuntimeError (with token location) instead of ClassCastException. */
private fun requireNumberOperands(operator: Token, left: Any?, right: Any?) = when {
    left is Double && right is Double -> left to right
    else -> throw RuntimeError(operator, "Operands must be numbers.")
}
