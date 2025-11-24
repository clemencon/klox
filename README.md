# Klox

A Kotlin implementation of the **Lox** programming language
from Robert Nystrom's [Crafting Interpreters](https://craftinginterpreters.com).

## Expedition Disclaimer

![k2.gif](k2.gif)

I'm acclimatizing to the terrain and unfamiliar with the equipment. This is my first ascent.
If you notice me doing something dangerous, shout a warning! Issues and pull requests are welcome.

No TDD, I know...
Following the book's structure, learning interpreter concepts together with Kotlin for now.
I will practice TDD for a second implementation,
perhaps with Thorsten Ball's [Writing An Interpreter In Go](https://interpreterbook.com).

## Grammar

```text
program        → declaration* EOF ;

declaration    → varDecl
               | statement ;
varDecl        → "var" IDENTIFIER ( "=" expression )? ";" ;

statement      → exprStmt
               | forStmt
               | ifStmt
               | printStmt
               | whileStmt
               | block ;

forStmt        → "for" "(" ( varDecl | exprStmt | ";" )
                  expression? ";"
                  expression? ")" statement ;

ifStmt         → "if" "(" expression ")" statement
               ( "else" statement )? ;

whileStmt      →  "while" "(" expression ")" statement ;

block          →  "{" declaration* "}" ;

expression     → assignment ;
assignment     → IDENTIFIER "=" assignment
               | logic_or ;
logic_or       → logic_and ( "or" logic_and )* ;
logic_and      → equality ( "and" equality )* ;
equality       → comparison ( ( "!=" | "==" ) comparison )* ;
comparison     → term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
term           → factor ( ( "-" | "+" ) factor )* ;
factor         → unary ( ( "/" | "*" ) unary )* ;
unary          → ( "!" | "-" ) unary | call ;
call           → primary ( "(" arguments? ")" )* ;
arguments      → expression ( "," expression )* ;
primary        → "true" | "false" | "nil"
               | NUMBER | STRING
               | "(" expression ")"
               | IDENTIFIER ;
```

**Reading the notation:** `→` = "produces", `|` separates alternatives, `*` = zero or more, `?` = optional.
Terminal symbols are in quotes (`"if"`), non-terminals are lowercase names.
