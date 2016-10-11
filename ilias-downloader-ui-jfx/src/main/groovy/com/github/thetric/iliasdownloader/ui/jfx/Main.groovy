package com.github.thetric.iliasdownloader.ui.jfx

import com.github.thetric.iliasdownloader.service.IliasService
import com.github.thetric.iliasdownloader.service.model.LoginCredentials
import com.github.thetric.iliasdownloader.ui.jfx.prefs.UserPreferenceService
import com.github.thetric.iliasdownloader.ui.jfx.prefs.UserPreferenceServiceImpl
import com.github.thetric.iliasdownloader.ui.jfx.prefs.UserPreferences
import com.github.thetric.iliasdownloader.ui.jfx.ui.intro.setup.WebIliasSetupController
import com.github.thetric.iliasdownloader.ui.jfx.ui.main.MainUi
import com.github.thetric.iliasdownloader.ui.jfx.ui.util.DialogHelper
import groovy.transform.CompileStatic
import javafx.application.Application
import javafx.application.Platform
import javafx.stage.Stage
import javafx.util.Callback
import javafx.util.Pair
import lombok.NonNull
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.controlsfx.dialog.LoginDialog

/**
 * Entry point for the JavaFX GUI.
 *
 * @author thetric
 * @since 30/01/2016
 */
@CompileStatic
final class Main extends Application {
    private static final Logger log = LogManager.logger
    private static final String ILIAS_DOWNLOADER_SETTINGS = 'iliasdownloader.yml'
    private final UserPreferenceService userPreferenceService

    Main() {
        this.userPreferenceService = new UserPreferenceServiceImpl(ILIAS_DOWNLOADER_SETTINGS)
    }

    static void main(String[] args) {
        launch(Main, args)
    }

    /**
     * The entry for the application.
     * <p>
     * First we try to load the user settings. If they do not exist a setup dialog for the Ilias URL shows up. If other
     * exceptions occur the application is terminated with an error dialog. Otherwise the dialog is skipped and a login
     * dialogs appears.
     *
     * @param ignored
     *         primary {@link Stage} - ignored by this program
     */
    @Override
    void start(Stage ignored) {
        def userPreferences
        try {
            userPreferences = userPreferenceService.loadUserPreferences()
                                                   .orElse(defaultPreferences)
        } catch (IOException e) {
            def message = 'Konnte die Einstellungen nicht laden.'
            log.error(message, e)
            DialogHelper.showExceptionDialog(message, e)
                        .ifPresent({ Platform.exit() })
            return
        }
        try {
            createIliasService(userPreferences.iliasServerURL, userPreferences.userName)
        } catch (Exception e) {
            def ioErrMsg = 'Fehler beim Erstellen des Ilias Connector'
            log.error(ioErrMsg, e)
            DialogHelper.showExceptionDialog(ioErrMsg, e)
        }
    }

    private UserPreferences getDefaultPreferences() {
        log.info('Keine Benutzereinstellungen gefunden. Lade Standardeinstellungen')
        def preferences = new UserPreferences()
        preferences.iliasServerURL = ''
        preferences.userName = ''
        return preferences
    }

    /**
     * Creates a {@link IliasService} and continues to the login dialog. If the user aborts the application shuts down.
     * If there is no default setting for the Ilias server url a setup dialog pops up which helps the user to define the
     * server connection settings.
     *
     * @param iliasServerBaseUrl
     *         base URL of the Ilias service, must not be {@code null} (empty string is permitted to trigger the setup
     *         dialog)
     * @param username
     *         user name for the Ilias login, must not be {@code null} (empty string is permitted to trigger the setup
     *         dialog)
     */
    private void createIliasService(@NonNull String iliasServerBaseUrl, @NonNull String username) {
        new WebIliasSetupController()
                .getIliasService(iliasServerBaseUrl)
                .ifPresent({ showLogin(it, username) })
    }

    /**
     * Shows the login and attempts to connect to the Ilias server with the given credentials. If the user aborts the
     * dialog the application will be shut down.
     *
     * @param iliasService
     *         the {@link IliasService} to login to
     * @param username
     *         user name for the Ilias login, must not be {@code null} (empty string is permitted to trigger the setup
     *         dialog)
     */
    private void showLogin(@NonNull IliasService iliasService, @NonNull String username) {
        new LoginDialog(new Pair<>(username, ''), {
            def credentials = fromPair(it as Pair<String, String>)
            iliasService.login(credentials)
            return null
        }).showAndWait()
          .ifPresent({ showMainUi() })
    }

    /**
     * Creates {@link LoginCredentials} from a {@link Pair}. The key is the user name, the value the password.
     *
     * @param usernamePasswordPair
     *         {@link Pair} from the login authenticator callback from {@link LoginDialog#LoginDialog(Pair, Callback)}
     * @return {@link LoginCredentials} with the given user name/password
     */
    private LoginCredentials fromPair(Pair<String, String> usernamePasswordPair) {
        return new LoginCredentials(usernamePasswordPair.key,
                usernamePasswordPair.value)
    }

    /**
     * Creates and shows the main UI. If any exception is propagated to this function an error dialog pops up and the
     * application shuts down. It is the responsibility of the called classes to properly handle any occurring
     * exceptions.
     */
    private void showMainUi() {
        log.info('Öffne Main UI')
        try {
            new MainUi(hostServices)
        } catch (IOException e) {
            log.error('Could not create the main ui', e)
            DialogHelper.showExceptionDialog('Fehler beim Laden der Main UI', e)
                        .ifPresent({ Platform.exit() })
        }
    }
}
