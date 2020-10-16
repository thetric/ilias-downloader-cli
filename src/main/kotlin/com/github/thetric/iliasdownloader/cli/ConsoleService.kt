package com.github.thetric.iliasdownloader.cli

import mu.KotlinLogging
import java.util.*

private val log = KotlinLogging.logger {}

/**
 * Reads the input either from the system environment or prompts the user for
 * input.
 */
class SystemEnvironmentAwareConsoleService  {
    private val scanner = Scanner(System.`in`)

    fun readLine(envVar: String, prompt: String): String {
        val property = System.getenv(envVar)
        if (property != null) {
            return property
        }
        print("$prompt: ")
        return scanner.nextLine()
    }

    /**
     * Returns the password from the specified system environment variable (if
     * present) or tries to prompt the user. **Important:** In most IDEs the
     * password prompt is unavailable so we're using plaintext input.
     * It is recommended to define system environment variables instead.
     *
     * @param envVar  System environment variable to load the password from
     * @param prompt  printed before the prompt
     * @return the password
     */
    fun readPassword(envVar: String, prompt: String): String {
        val credentials = System.getenv(envVar)
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
        return scanner.nextLine()
    }

    private fun isNotRunningInAnIde(): Boolean = System.console() != null
}
