package com.github.thetric.iliasdownloader.cli

import com.github.thetric.iliasdownloader.cli.console.ConsoleService
import com.github.thetric.iliasdownloader.cli.console.SystemEnvironmentAwareConsoleService
import com.github.thetric.iliasdownloader.ui.common.prefs.JsonUserPreferenceService
import com.github.thetric.iliasdownloader.ui.common.prefs.UserPreferenceService
import groovy.transform.CompileStatic
import groovy.transform.TupleConstructor
import groovy.util.logging.Log4j2

import java.nio.file.Path

import static org.apache.logging.log4j.Level.TRACE

/**
 * Entry point for the CLI.
 */
@Log4j2
@TupleConstructor
@CompileStatic
final class Cli {
    // NOTE DO NOT launch in IntelliJ with 'Delegate IDE build/run actions to gradle' as no console is then available!
    // instead you should define system environment variables (see SyncController for the var names)

    private static final String SETTINGS_FILE_NAME = '.ilias-downloader.json'

    static void main(final String[] args) {
        final ResourceBundle resourceBundle = ResourceBundle.getBundle('ilias-cli')
        final cliService = new CliService(resourceBundle)
        try {
            final cliOptions = cliService.parseOpts(args)
            final Path settingsPath = cliOptions.syncDir.resolve(SETTINGS_FILE_NAME)
            final ConsoleService consoleService = new SystemEnvironmentAwareConsoleService()
            final UserPreferenceService preferenceService = new JsonUserPreferenceService(settingsPath)
            new CliController(resourceBundle, cliOptions, settingsPath, consoleService, preferenceService).startCliController()
        } catch (final InvalidUsageException ue) {
            log.catching(TRACE, ue)
        }
    }

}
