package de.adesso.azubis.iliasdownloader.v3;

import de.adesso.azubis.iliasdownloader.v3.prefs.UserPreferenceService;
import de.adesso.azubis.iliasdownloader.v3.prefs.UserPreferenceServiceImpl;
import de.adesso.azubis.iliasdownloader.v3.prefs.UserPreferences;
import de.adesso.azubis.iliasdownloader.v3.ui.intro.setup.WebIliasSetupController;
import de.adesso.azubis.iliasdownloader.v3.ui.main.MainUi;
import de.adesso.azubis.iliasdownloader.v3.ui.util.DialogHelper;
import de.adesso.iliasdownloader3.service.IliasService;
import de.adesso.iliasdownloader3.service.model.LoginCredentials;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Pair;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.controlsfx.dialog.LoginDialog;

import java.io.IOException;

/**
 * Entry point for the JavaFX GUI.
 *
 * @author thetric
 * @since 30/01/2016
 */
@Log4j2
public final class Main extends Application {
    private static final String ILIAS_DOWNLOADER_SETTINGS = "iliasdownloader.xml";
    private final UserPreferenceService userPreferenceService;

    public Main() {
        this.userPreferenceService = new UserPreferenceServiceImpl(ILIAS_DOWNLOADER_SETTINGS);
    }

    public static void main(String[] args) {
        Main.launch(args);
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
    public void start(Stage ignored) {
        final UserPreferences userPreferences;
        try {
            userPreferences = userPreferenceService.loadUserPreferences()
                                                   .orElseGet(this::getDefaultPreferences);
        } catch (IOException e) {
            String message = "Konnte die Einstellungen nicht laden.";
            log.error(message, e);
            DialogHelper.showExceptionDialog(message, e)
                        .ifPresent(c -> Platform.exit());
            return;
        }
        try {
            createIliasService(userPreferences.getIliasServerURL(), userPreferences.getUserName());
        } catch (Exception e) {
            log.error("Fehler beim Erstellen des Ilias Connector", e);
            DialogHelper.showExceptionDialog("Fehler beim Erstellen des Ilias Connector", e);
        }
    }

    private UserPreferences getDefaultPreferences() {
        log.info("Keine Benutzereinstellungen gefunden. Lade Standardeinstellungen");
        UserPreferences preferences = new UserPreferences();
        preferences.setIliasServerURL("");
        preferences.setUserName("");
        return preferences;
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
                .ifPresent(iliasService -> showLogin(iliasService, username));
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
        new LoginDialog(new Pair<>(username, ""), usernamePasswordPair -> {
            LoginCredentials credentials = fromPair(usernamePasswordPair);
            iliasService.login(credentials);
            return null;
        }).showAndWait()
          .ifPresent(ignoredCredentials -> showMainUi());
    }

    /**
     * Creates {@link LoginCredentials} from a {@link Pair}. The key is the user name, the value the password.
     *
     * @param usernamePasswordPair
     *         {@link Pair} from the login authenticator callback from {@link LoginDialog#LoginDialog(Pair, Callback)}
     * @return {@link LoginCredentials} with the given user name/password
     */
    private LoginCredentials fromPair(Pair<String, String> usernamePasswordPair) {
        return new LoginCredentials(usernamePasswordPair.getKey(),
                                    usernamePasswordPair.getValue());
    }

    /**
     * Creates and shows the main UI. If any exception is propagated to this function an error dialog pops up and the
     * application shuts down. It is the responsibility of the called classes to properly handle any occurring
     * exceptions.
     */
    private void showMainUi() {
        log.info("Öffne Main UI");
        try {
            new MainUi();
        } catch (IOException e) {
            log.error("Could not create the main ui", e);
            DialogHelper.showExceptionDialog("Fehler beim Laden der Main UI", e)
                        .ifPresent(res -> Platform.exit());
        }
    }
}
