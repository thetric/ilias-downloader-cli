package com.github.thetric.iliasdownloader.cli

import com.github.thetric.iliasdownloader.cli.console.SystemEnvironmentAwareConsoleService
import com.github.thetric.iliasdownloader.ui.common.prefs.JsonPreferenceService
import com.github.thetric.iliasdownloader.ui.common.prefs.PreferenceService
import com.github.thetric.iliasdownloader.ui.common.prefs.UserPreferences
import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.mainBody
import org.apache.logging.log4j.Level.TRACE
import org.apache.logging.log4j.LogManager
import java.util.ResourceBundle

private const val SETTINGS_FILE_NAME = ".ilias-downloader.json"
private val log = LogManager.getLogger(Cli::class.java)

class Cli

/**
 * Entry point for the CLI.
 */
fun main(args: Array<String>) {
    val resourceBundle = ResourceBundle.getBundle("ilias-cli")
    try {
        val cliOptions = mainBody("Ilias Downloader (CLI)") {
            CliOptions(ArgParser(args), resourceBundle)
        }
        val settingsPath = cliOptions.syncDir.resolve(SETTINGS_FILE_NAME)
        log.info("Settings path: {}", settingsPath.toAbsolutePath())
        val consoleService = SystemEnvironmentAwareConsoleService()
        val preferenceService: PreferenceService<UserPreferences> = JsonPreferenceService(settingsPath, UserPreferences::class.java)
        CliController(resourceBundle, cliOptions, consoleService, preferenceService).startCliController()
    } catch (ue: InvalidUsageException) {
        log.catching(TRACE, ue)
    }
}
