package com.github.thetric.iliasdownloader.cli

/**
 * @author broj
 * @since 16.01.2017
 */
interface ConsoleService {

    String readLine(String systemProp, String prompt)

    String readPassword(String systemProp, String prompt)
}
