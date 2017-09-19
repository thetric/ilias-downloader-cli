package com.github.thetric.iliasdownloader.cli.console

import mu.KotlinLogging
import java.util.Scanner

private val log = KotlinLogging.logger {}

/**
 * Reads the input either from the system environment or prompts the user for input.
 *
 * @author broj
 * @since 16.01.2017
 */
class SystemEnvironmentAwareConsoleService : ConsoleService {
    override fun readLine(systemProp: String, prompt: String): String {
        val property = System.getProperty(systemProp)
        if (property != null) {
            return property
        }
        print("$prompt: ")
        return readNextLine()
    }

    /**
     * Returns the password from the specified system environment variable (if present) or tries to prompt the user. **Important:** In
     * most IDEs the password prompt is unavailable so we're using plaintext input. It is recommended to define system environment variables
     * instead.
     *
     * @param systemProp
     * System environment variable to load the password from
     * @param prompt
     * printed before the prompt
     * @return the password
     */
    override fun readPassword(systemProp: String, prompt: String): String {
        val credentials = System.getProperty(systemProp)
        if (credentials != null) {
            return credentials
        }

        print("$prompt: ")
        if (isNotRunningInAnIde()) {
            val readPassword = System.console().readPassword()
            return String(readPassword)
        }

        log.warn("Password input in IDEs are _not_ supported")
        log.warn("FALLING BACK TO PLAIN TEXT MODE")
        return readNextLine()
    }

    private fun isNotRunningInAnIde(): Boolean = System.console() != null

    private fun readNextLine(): String = Scanner(System.`in`).use { return it.nextLine() }
}
