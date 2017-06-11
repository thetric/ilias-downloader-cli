package com.github.thetric.iliasdownloader.cli

import com.github.thetric.iliasdownloader.cli.console.ConsoleService
import com.github.thetric.iliasdownloader.cli.sync.ItemDownloadingItemVisitor
import com.github.thetric.iliasdownloader.service.IliasItemVisitor
import com.github.thetric.iliasdownloader.service.IliasService
import com.github.thetric.iliasdownloader.service.exception.IliasAuthenticationException
import com.github.thetric.iliasdownloader.service.webparser.CookieService
import com.github.thetric.iliasdownloader.service.webparser.JsoupCookieService
import com.github.thetric.iliasdownloader.service.webparser.WebParserIliasServiceProvider
import com.github.thetric.iliasdownloader.ui.common.prefs.UserPreferenceService
import com.github.thetric.iliasdownloader.ui.common.prefs.UserPreferences
import groovy.transform.CompileStatic
import groovy.util.logging.Log4j2

import java.nio.file.Path
import java.util.function.Function

/**
 * Connects to the Ilias and starts the sync.
 */
@CompileStatic
@Log4j2
final class CliController {
    private final ResourceBundle resourceBundle
    private final CliOptions cliOptions

    private final Path settingsPath
    private final UserPreferenceService preferenceService
    private final ConsoleService consoleService

    CliController(
        final ResourceBundle resourceBundle, final CliOptions cliOptions, final Path settingsPath,
        final ConsoleService consoleService, final UserPreferenceService preferenceService) {
        this.resourceBundle = resourceBundle
        this.cliOptions = cliOptions
        this.settingsPath = settingsPath
        this.consoleService = consoleService
        this.preferenceService = preferenceService
    }

    void startCliController() {
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

}
