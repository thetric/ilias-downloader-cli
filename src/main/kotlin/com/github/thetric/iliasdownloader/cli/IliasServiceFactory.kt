package com.github.thetric.iliasdownloader.cli

import com.github.thetric.iliasdownloader.cli.preferences.JsonPreferenceService
import com.github.thetric.iliasdownloader.cli.preferences.UserPreferences
import com.github.thetric.iliasdownloader.connector.IliasService
import com.github.thetric.iliasdownloader.connector.model.LoginCredentials
import mu.KotlinLogging
import java.nio.file.Files
import java.text.MessageFormat
import java.util.*

private val log = KotlinLogging.logger {}

/**
 * Creates a [IliasService] from a settings file (if found) or a setup dialog.
 */
internal class IliasServiceFactory(
    private val iliasProvider: (String) -> IliasService,
    private val resourceBundle: ResourceBundle,
    private val preferenceService: JsonPreferenceService,
    private val consoleService: SystemEnvironmentAwareConsoleService) {

    /**
     * Creates a new connected [IliasService].
     * The settings for the connection are applied either from previous settings
     * (if available) or from a setup dialog.
     * In the latter case the preferences are saved.
     *
     * @return connected [IliasService]
     */
    fun connect(): IliasService {
        return if (Files.exists(preferenceService.settingsFile)) {
            createServiceFromConfig()
        } else createFromFirstTimeSetup()
    }

    private fun createServiceFromConfig(): IliasService {
        log.debug {
            val absolutePath = preferenceService.settingsFile.toAbsolutePath()
            "Trying to load existing config from $absolutePath"
        }
        val (iliasServerURL, userName) = preferenceService.loadPreferences()
        val iliasService = iliasProvider(iliasServerURL)
        val password = promptForPassword(userName)
        iliasService.login(LoginCredentials(userName, password))
        return iliasService
    }

    private fun promptForPassword(username: String): String {
        val passwordPrompt = MessageFormat.format(
            resourceBundle.getString("login.credentials.password"),
            username
        )
        return consoleService.readPassword(
            "ilias.credentials.password",
            passwordPrompt
        )
    }

    private fun createFromFirstTimeSetup(): IliasService {
        log.debug { "No existing config found, starting first time setup" }
        val iliasLoginUrl =
            consoleService.readLine("ilias.server.url", "Ilias Server URL")
        val iliasService = iliasProvider(iliasLoginUrl)
        val credentials = promptForCredentials()
        iliasService.login(credentials)
        val prefs = UserPreferences(iliasLoginUrl, credentials.userName, 0)
        preferenceService.savePreferences(prefs)
        return iliasService
    }

    private fun promptForCredentials(): LoginCredentials {
        val userName = promptForUserName()
        val password = promptForPassword(userName)
        return LoginCredentials(userName, password)
    }

    private fun promptForUserName(): String {
        val usernamePrompt =
            resourceBundle.getString("login.credentials.username")
        return consoleService.readLine(
            "ilias.credentials.username",
            usernamePrompt
        ).trim { it <= ' ' }
    }
}
