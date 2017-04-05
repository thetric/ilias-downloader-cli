package com.github.thetric.iliasdownloader.cli.configloader

import com.github.thetric.iliasdownloader.cli.console.ConsoleService
import com.github.thetric.iliasdownloader.service.IliasService
import com.github.thetric.iliasdownloader.service.exception.IliasAuthenticationException
import com.github.thetric.iliasdownloader.service.model.LoginCredentials
import com.github.thetric.iliasdownloader.ui.common.prefs.UserPreferenceService
import com.github.thetric.iliasdownloader.ui.common.prefs.UserPreferences
import groovy.transform.CompileStatic
import groovy.transform.TupleConstructor
import groovy.util.logging.Log4j2
import org.apache.logging.log4j.Level

import java.util.function.Function

@TupleConstructor
@Log4j2
@CompileStatic
final class ExistingConfigCliController {
    Function<String, IliasService> iliasProvider
    ResourceBundle resourceBundle
    UserPreferenceService preferenceService
    ConsoleService consoleService

    IliasService createIliasService() {
        UserPreferences prefs = preferenceService.loadUserPreferences()
        IliasService iliasService = iliasProvider.apply(prefs.iliasServerURL)
        promptForPassword(iliasService, prefs)
        return iliasService
    }

    private void promptForPassword(IliasService iliasService, UserPreferences prefs) {
        try {
            String passwordPrompt = resourceBundle.getString('login.credentials.password')
            String namePwPrompt = String.format(passwordPrompt, prefs.userName)
            String password = consoleService.readPassword('ilias.credentials.password', namePwPrompt)
            iliasService.login(new LoginCredentials(prefs.userName, password))
        } catch (IliasAuthenticationException authEx) {
            log.catching(Level.DEBUG, authEx)
        }
    }
}
