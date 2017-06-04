package com.github.thetric.iliasdownloader.cli

import com.github.thetric.iliasdownloader.cli.console.ConsoleService
import com.github.thetric.iliasdownloader.cli.console.SystemEnvironmentAwareConsoleService
import com.github.thetric.iliasdownloader.cli.sync.ItemDownloadingItemVisitor
import com.github.thetric.iliasdownloader.service.IliasItemVisitor
import com.github.thetric.iliasdownloader.service.IliasService
import com.github.thetric.iliasdownloader.service.exception.IliasAuthenticationException
import com.github.thetric.iliasdownloader.service.webparser.CookieService
import com.github.thetric.iliasdownloader.service.webparser.JsoupCookieService
import com.github.thetric.iliasdownloader.service.webparser.WebParserIliasServiceProvider
import com.github.thetric.iliasdownloader.ui.common.prefs.JsonUserPreferenceService
import com.github.thetric.iliasdownloader.ui.common.prefs.UserPreferenceService
import com.github.thetric.iliasdownloader.ui.common.prefs.UserPreferences
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

    private final ResourceBundle resourceBundle
    private final CliOptions cliOptions

    private final Path settingsPath
    private final UserPreferenceService preferenceService
    private final ConsoleService consoleService = new SystemEnvironmentAwareConsoleService()

    static void main(final String[] args) {
        final ResourceBundle resourceBundle = ResourceBundle.getBundle('ilias-cli')
        final cliService = new CliService(resourceBundle)
        try {
            final opts = cliService.parseOpts(args)
            new Cli(resourceBundle, opts).startCliController()
        } catch (final InvalidUsageException ue) {
            log.catching(TRACE, ue)
        }
    }

    Cli(final ResourceBundle resourceBundle, final CliOptions cliOptions) {
        this.resourceBundle = resourceBundle
        this.cliOptions = cliOptions
        settingsPath = cliOptions.syncDir.resolve(SETTINGS_FILE_NAME)
        preferenceService = new JsonUserPreferenceService(settingsPath)
    }

    private IliasService createIliasService() {
        final CookieService cookieService = new JsoupCookieService()
        final Function<String, IliasService> iliasProvider = { final String url ->
            new WebParserIliasServiceProvider(cookieService, url).newInstance()
        }
        final LoginService loginService = new LoginServiceImpl(
            iliasProvider,
            resourceBundle,
            preferenceService,
            consoleService)

        return loginService.connect()
    }

    private void startCliController() {
        try {
            final IliasService iliasService = createIliasService()
            final Function<UserPreferences, ? extends IliasItemVisitor> syncHandlerProvider = {
                final UserPreferences prefs -> return new ItemDownloadingItemVisitor(cliOptions.syncDir, iliasService, prefs)
            }
            new IliasCliController(
                cliOptions,
                iliasService,
                syncHandlerProvider,
                resourceBundle,
                preferenceService,
                consoleService).start()
        } catch (final IliasAuthenticationException authEx) {
            log.error(resourceBundle.getString('login.error'), authEx)
        }
    }
}
