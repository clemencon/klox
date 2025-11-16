package dev.clemencon.klox.parser

/**
 * Sentinel exception for unwinding the parser on errors.
 * Error reporting happens via Lox.error() before throwing this.
 * Using an exception allows immediate exit from nested parsing methods
 * without wrapping return types in Result-style containers.
 */
class ParseError : RuntimeException()