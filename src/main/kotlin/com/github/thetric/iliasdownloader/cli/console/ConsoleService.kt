package com.github.thetric.iliasdownloader.cli.console

/**
 * Simpler interface to the console.
 */
interface ConsoleService {
    fun readLine(systemProp: String, prompt: String): String

    fun readPassword(systemProp: String, prompt: String): String
}
