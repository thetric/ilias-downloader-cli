package com.github.thetric.iliasdownloader.cli

import com.github.thetric.iliasdownloader.cli.console.ConsoleService
import com.github.thetric.iliasdownloader.service.IliasService
import com.github.thetric.iliasdownloader.service.model.LoginCredentials
import com.github.thetric.iliasdownloader.ui.common.prefs.UserPreferenceService
import com.github.thetric.iliasdownloader.ui.common.prefs.UserPreferences
import groovy.transform.CompileStatic
import groovy.util.logging.Log4j2

import java.nio.file.Files
import java.util.function.Function

/**
 * Creates a {@link IliasService} from a settings file (if found) or a setup dialog.
 */
@Log4j2
@CompileStatic
final class LoginServiceImpl implements LoginService {
    private final Function<String, IliasService> iliasProvider
    private final ResourceBundle resourceBundle
    private final UserPreferenceService preferenceService
    private final ConsoleService consoleService

    LoginServiceImpl(
        final Function<String, IliasService> iliasProvider,
        final ResourceBundle resourceBundle,
        final UserPreferenceService preferenceService, final ConsoleService consoleService) {
        this.iliasProvider = iliasProvider
        this.resourceBundle = resourceBundle
        this.preferenceService = preferenceService
        this.consoleService = consoleService
    }

    @Override
    IliasService connect() {
        return Files.exists(preferenceService.settingsFile) ? createServiceFromConfig() : createFromFirstTimeSetup()
    }

    private IliasService createServiceFromConfig() {
        final UserPreferences prefs = preferenceService.loadUserPreferences()
        final IliasService iliasService = iliasProvider.apply(prefs.iliasServerURL)
        final String password = promptForPassword(prefs.userName)
        iliasService.login(new LoginCredentials(userName: prefs.userName, password: password))
        return iliasService
    }

    private String promptForPassword(final String username) {
        final String passwordPrompt = resourceBundle.getString('login.credentials.password')
        final String namePwPrompt = String.format(passwordPrompt, username)
        return consoleService.readPassword('ilias.credentials.password', namePwPrompt)
    }

    // first time init stuff

    private IliasService createFromFirstTimeSetup() {
        final String iliasLoginUrl = consoleService.readLine('ilias.server.url', 'Ilias Server URL')
        final IliasService iliasService = iliasProvider.apply(iliasLoginUrl)
        final LoginCredentials credentials = promptForCredentials()
        iliasService.login(credentials)
        final UserPreferences prefs = new UserPreferences(iliasLoginUrl, credentials.userName, 0)
        preferenceService.saveUserPreferences(prefs)
        return iliasService
    }

    private LoginCredentials promptForCredentials() {
        final String userName = promptForUserName()
        final String password = promptForPassword(userName)
        return new LoginCredentials(userName: userName, password: password)
    }

    private String promptForUserName() {
        final String usernamePrompt = resourceBundle.getString('login.credentials.username')
        return consoleService.readLine('ilias.credentials.username', usernamePrompt).trim()
    }
}
