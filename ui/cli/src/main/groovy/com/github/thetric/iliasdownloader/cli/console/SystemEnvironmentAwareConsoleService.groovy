package com.github.thetric.iliasdownloader.cli.console

import groovy.transform.CompileStatic
import groovy.util.logging.Log4j2

/**
 * Reads the input either from the system environment or prompts the user for input.
 * @author broj
 * @since 16.01.2017
 */
@Log4j2
@CompileStatic
final class SystemEnvironmentAwareConsoleService implements ConsoleService {
    @Override
    String readLine(String systemProp, String prompt) {
        String property = System.getProperty(systemProp)
        if (property) {
            return property
        }

        print "$prompt: "
        Scanner scanner = new Scanner(System.in)
        return scanner.nextLine()
    }

    /**
     * Returns the password from the specified system environment variable (if present) or tries to prompt the user.
     * <b>Important:</b> In most IDEs the password prompt is unavailable so we're using plaintext input. It is
     * recommended to define system environment variables instead.
     *
     * @param systemProp System environment variable to load the password from
     * @param prompt printed before the prompt
     * @return the password
     */
    @Override
    String readPassword(String systemProp, String prompt) {
        String credentials = System.getProperty(systemProp)
        if (credentials) {
            return credentials
        }

        print "$prompt: "
        if (System.console()) {
            return System.console().readPassword().toString()
        }
        log.warn('Password input in IDEs are _not_ supported')
        log.warn('FALLING BACK TO PLAIN TEXT MODE')
        Scanner s = new Scanner(System.in)
        return s.nextLine()
    }
}
