package com.github.thetric.iliasdownloader.cli

import com.github.thetric.iliasdownloader.cli.preferences.JsonPreferenceService
import com.github.thetric.iliasdownloader.connector.IliasService
import com.github.thetric.iliasdownloader.connector.exception.IliasAuthenticationException
import com.github.thetric.iliasdownloader.connector.WebParserIliasServiceProvider
import mu.KotlinLogging
import java.util.ResourceBundle

private val log = KotlinLogging.logger {}

/**
 * Connects to the Ilias and starts the sync.
 */
internal class CliController(
    private val resourceBundle: ResourceBundle,
    private val cliOptions: CliOptions,
    private val consoleService: SystemEnvironmentAwareConsoleService,
    private val preferenceService: JsonPreferenceService
) {

    fun startCliController() {
        val iliasService = connectToIlias()
        try {
            val preferencesUpdateService = UserPreferencesUpdateService(
                iliasService,
                resourceBundle,
                preferenceService,
                consoleService
            )
            val syncSettings =
                preferencesUpdateService.updatePreferences(cliOptions)
            val itemVisitor = ItemDownloadingItemListener(
                iliasService,
                resourceBundle,
                syncSettings.maxFileSizePerFileInMiB
            )
            val syncController = SyncController(
                iliasService,
                itemVisitor,
                resourceBundle,
                cliOptions.syncDir
            )
            syncController.startSync(syncSettings.courses)
        } catch (authEx: IliasAuthenticationException) {
            log.error(authEx) { resourceBundle.getString("login.error") }
        } finally {
            iliasService.close()
        }
    }

    private fun connectToIlias(): IliasService {
        val iliasProvider = { url: String ->
            WebParserIliasServiceProvider(url).newInstance()
        }

        val loginService = IliasServiceFactory(
            iliasProvider,
            resourceBundle,
            preferenceService,
            consoleService
        )

        return loginService.connect()
    }
}
