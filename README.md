# Klox

A Kotlin implementation of the **Lox** programming language
from Robert Nystrom's [Crafting Interpreters](https://craftinginterpreters.com).

## Expedition Disclaimer

![k2.gif](k2.gif)

I'm acclimatizing to the terrain and unfamiliar with the equipment.
If you notice me doing something dangerous, shout a warning (or open a PR)!

No TDD, I know...
Following the book's structure, learning interpreter concepts together with Kotlin for now.
I will practice TDD for a second implementation,
perhaps with Thorsten Ball's [Writing An Interpreter In Go](https://interpreterbook.com).

## Quick Start

Coming up.

## What Works

- **Variables and arithmetic**: Declare variables, do math, concatenate strings
- **Control flow**: if/else, while, for loops
- **Logical operators**: and, or with short-circuiting
- **Scoping**: Block-level variable scopes
- **REPL**: Interactive prompt with persistent state
- **Native functions**: `clock()` returns current time

## What Doesn't Work Yet

Functions are partially implemented (parsing works, execution doesn't).

## Example Code

Variables and arithmetic:

```lox
var a = 10;
var b = 20;
print a + b; // 42
```

Control flow:

```lox
var x = 5;
if (x > 3) {
    print "x is big";
} else {
    print "x is small";
}
```

Loops:

```lox
var i = 0;
while (i < 5) {
    print i;
    i = i + 1;
}
```

Functions:

```lox
fun fibonachos(n) {
    if (n <= 1) return n;
    return fibonachos(n - 2) + fib(n - 1);
}

print fibonachos(10);
```

## Architecture

Three-stage pipeline:

1. Scanner: Source code → tokens
2. Parser: Tokens → AST
3. Interpreter: AST → execution

Entry point: `Main.kt` handles REPL and file execution.
