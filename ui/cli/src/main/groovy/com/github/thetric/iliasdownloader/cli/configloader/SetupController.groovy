package com.github.thetric.iliasdownloader.cli.configloader

import com.github.thetric.iliasdownloader.cli.console.ConsoleService
import com.github.thetric.iliasdownloader.service.IliasService
import com.github.thetric.iliasdownloader.service.exception.IliasAuthenticationException
import com.github.thetric.iliasdownloader.service.model.LoginCredentials
import com.github.thetric.iliasdownloader.ui.common.prefs.UserPreferenceService
import com.github.thetric.iliasdownloader.ui.common.prefs.UserPreferences
import groovy.transform.TupleConstructor
import groovy.util.logging.Log4j2
import org.apache.logging.log4j.Level

import java.util.function.Function

@Log4j2
@TupleConstructor
class SetupController {
    Function<String, IliasService> iliasProvider
    ResourceBundle resourceBundle
    UserPreferenceService preferenceService
    ConsoleService consoleService

    private final UserPreferences prefs = new UserPreferences()

    IliasService createIliasService() {
        IliasService iliasService = createIliasServiceFromUserUrl()
        log.info('Connected!')
        login(iliasService)
        log.info(resourceBundle.getString('login.successful'))
        preferenceService.saveUserPreferences(prefs)

        return iliasService
    }

    private IliasService createIliasServiceFromUserUrl() {
        while (true) {
            String serverUrl = promptForServerUrl()
            try {
                IliasService iliasService = iliasProvider.apply(serverUrl)
                prefs.iliasServerURL = serverUrl
                return iliasService
            } catch (RuntimeException e) {
                log.catching(e)
            }
        }
    }

    private promptForServerUrl() {
        return consoleService.readLine('ilias.server.url', 'Ilias Server URL')
    }

    private login(IliasService iliasService) {
        log.info('Prompting for credentials')

        try {
            String usernamePrompt = resourceBundle.getString('login.credentials.username')
            String username = consoleService.readLine('ilias.credentials.username', usernamePrompt)
            String passwordPrompt = resourceBundle.getString('login.credentials.password')
            String password = consoleService.readPassword('ilias.credentials.password', passwordPrompt)

            iliasService.login(new LoginCredentials(username, password))
            prefs.userName = username
            preferenceService.saveUserPreferences(prefs)
        } catch (IliasAuthenticationException authEx) {
            log.catching(Level.DEBUG, authEx)
            log.error(resourceBundle.getString('login.error'))
        }
    }
}
