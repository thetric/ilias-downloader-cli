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
import javafx.util.Pair;
import lombok.extern.log4j.Log4j2;
import org.controlsfx.dialog.LoginDialog;

import java.io.IOException;
import java.util.Optional;

/**
 * Startklasse der Anwendung.
 *
 * @author Dominik Broj
 * @since 30.01.2016
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

    @Override
    public void start(Stage stage) {
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

    private void createIliasService(String iliasServerBaseUrl, String username) {
        Optional<IliasService> iliasServiceOptional = new WebIliasSetupController().getIliasService(iliasServerBaseUrl);
        if (iliasServiceOptional.isPresent()) {
            showLogin(iliasServiceOptional.get(), username);
        } else {
            Platform.exit();
        }
    }

    private void showLogin(IliasService iliasService, String username) {
        new LoginDialog(new Pair<>(username, ""), usernamePasswordPair -> {
            LoginCredentials credentials = new LoginCredentials(usernamePasswordPair.getKey(),
                                                                usernamePasswordPair.getValue());
            iliasService.login(credentials);
            return null;
        }).showAndWait().ifPresent(e -> showMainUi());
    }

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
