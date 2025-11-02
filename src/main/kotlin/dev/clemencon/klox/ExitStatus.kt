package dev.clemencon.klox

/**
 * Exit codes following
 * [sysexits](https://man.freebsd.org/cgi/man.cgi?query=sysexits&apropos=0&sektion=0&manpath=FreeBSD+4.3-RELEASE).
 */
enum class ExitStatus(val code: Int) {
    OK(code = 0),
    USAGE(code = 64),
    DATAERR(code = 65)
}