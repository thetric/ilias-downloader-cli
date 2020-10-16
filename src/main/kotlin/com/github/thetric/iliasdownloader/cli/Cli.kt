package com.github.thetric.iliasdownloader.cli

import com.github.thetric.iliasdownloader.cli.preferences.JsonPreferenceService
import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.mainBody
import mu.KotlinLogging
import java.util.*

private const val SETTINGS_FILE_NAME = ".ilias-downloader.json"
private val log = KotlinLogging.logger {}

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
        log.info { "Settings path: ${settingsPath.toAbsolutePath()}" }
        val consoleService = SystemEnvironmentAwareConsoleService()
        val preferenceService = JsonPreferenceService(settingsPath)
        CliController(
            resourceBundle,
            cliOptions,
            consoleService,
            preferenceService
        ).startCliController()
    } catch (ue: InvalidUsageException) {
        log.trace(ue) { "Startup failed" }
    }
}
