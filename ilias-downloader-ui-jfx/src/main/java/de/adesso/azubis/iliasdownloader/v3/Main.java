package de.adesso.azubis.iliasdownloader.v3;

import de.adesso.azubis.iliasdownloader.v3.prefs.UserPreferenceService;
import de.adesso.azubis.iliasdownloader.v3.prefs.UserPreferenceServiceImpl;
import de.adesso.azubis.iliasdownloader.v3.prefs.UserPreferences;
import javafx.application.Application;
import javafx.collections.ObservableMap;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.nio.file.NoSuchFileException;

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
    public void start(Stage primaryStage) throws IOException {
        try {
            final UserPreferences userPreferences = userPreferenceService.loadUserPreferences();
            // show main ui
        } catch (NoSuchFileException noSettingsEx) {
            log.info("Keine Benutzereinstellungen gefunden, zeige Einrichtungsdialog.\n Exception: {}", noSettingsEx.getLocalizedMessage());
        }
    }

    private void mapWizardSettingsToUserPrefs(ObservableMap<String, Object> settings) {
        final UserPreferences prefs = new UserPreferences();
        prefs.setIliasServerURL((String) settings.get("iliasUrlField"));
    }
}
