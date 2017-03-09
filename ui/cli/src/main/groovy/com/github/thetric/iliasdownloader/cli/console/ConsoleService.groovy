package com.github.thetric.iliasdownloader.cli.console

interface ConsoleService {

    String readLine(String systemProp, String prompt)

    String readPassword(String systemProp, String prompt)
}
