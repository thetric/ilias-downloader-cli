package com.github.thetric.iliasdownloader.cli

import com.github.thetric.iliasdownloader.cli.console.ConsoleService
import com.github.thetric.iliasdownloader.cli.console.SystemEnvironmentAwareConsoleService
import com.github.thetric.iliasdownloader.service.IliasService
import com.github.thetric.iliasdownloader.service.webparser.CookieService
import com.github.thetric.iliasdownloader.service.webparser.JsoupCookieService
import com.github.thetric.iliasdownloader.service.webparser.WebParserIliasServiceProvider
import com.github.thetric.iliasdownloader.ui.common.prefs.JsonUserPreferenceService
import com.github.thetric.iliasdownloader.ui.common.prefs.UserPreferenceService
import groovy.transform.CompileStatic
import groovy.transform.TupleConstructor
import groovy.util.logging.Log4j2

import java.nio.file.Path
import java.util.function.Function

import static org.apache.logging.log4j.Level.TRACE

@Log4j2
@TupleConstructor
@CompileStatic
final class Cli {
    // NOTE DO NOT launch in IntelliJ with 'Delegate IDE build/run actions to gradle' as no console is then available!
    // instead you should define system environment variables (see IliasCliController for the var names)

    private static final String SETTINGS_FILE_NAME = '.ilias-downloader.json'

    static void main(String[] args) {
        try {
            ResourceBundle resourceBundle = ResourceBundle.getBundle('ilias-cli')
            final cliService = new CliService(resourceBundle)
            final opts = cliService.parseOpts(args)
            startCliController(opts, resourceBundle)
        } catch (InvalidUsageException ue) {
            log.catching(TRACE, ue)
        }
    }

    private static void startCliController(CliOptions cliOptions, ResourceBundle resourceBundle) {
        Path settingsPath = cliOptions.syncDir.resolve(SETTINGS_FILE_NAME)
        UserPreferenceService preferenceService = new JsonUserPreferenceService(settingsPath)
        final ConsoleService consoleService = new SystemEnvironmentAwareConsoleService()
        final CookieService cookieService = new JsoupCookieService()
        Function<String, IliasService> webIliasServiceProvider = { String url ->
            new WebParserIliasServiceProvider(cookieService, url).newInstance()
        }

        new IliasCliController(cliOptions, webIliasServiceProvider, resourceBundle, preferenceService, consoleService).
            start()
    }
}
