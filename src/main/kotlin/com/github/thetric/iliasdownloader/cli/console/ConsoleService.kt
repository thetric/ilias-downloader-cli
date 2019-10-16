package com.github.thetric.iliasdownloader.cli.console

/**
 * Simpler interface to the console.
 */
interface ConsoleService {
    fun readLine(envVar: String, prompt: String): String

    fun readPassword(envVar: String, prompt: String): String
}
