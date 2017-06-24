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
            final UserPreferencesUpdateService preferencesUpdateService = new UserPreferencesUpdateServiceImpl(
                iliasService,
                resourceBundle,
                preferenceService,
                consoleService)
            final SyncSettings syncSettings = preferencesUpdateService.updatePreferences(cliOptions)
            final IliasItemVisitor itemVisitor = new ItemDownloadingItemVisitor(
                cliOptions.syncDir,
                iliasService,
                syncSettings.maxFileSizePerFileInMiB)
            final SyncController syncController = new SyncController(
                iliasService,
                itemVisitor,
                resourceBundle,
                preferenceService,
                consoleService)
            syncController.startSync(syncSettings.courses)
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
