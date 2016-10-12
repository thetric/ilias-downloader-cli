package com.github.thetric.iliasdownloader.ui.jfx.prefs

import groovy.transform.CompileStatic
import groovy.util.logging.Log4j2
import lombok.NonNull
import org.yaml.snakeyaml.Yaml

import java.nio.charset.StandardCharsets
import java.nio.file.NoSuchFileException
import java.nio.file.Paths

/**
 * Created by Dominik Broj on 31.01.2016.
 *
 * @author Dominik Broj
 * @since 31.01.2016
 */
@CompileStatic
@Log4j2
final class UserPreferenceServiceImpl implements UserPreferenceService {
    private final String settingsFilename
    private final Yaml yaml

    UserPreferenceServiceImpl(@NonNull String userSettingsFilename) {
        this.settingsFilename = userSettingsFilename
        yaml = new Yaml()
    }

    @Override
    Optional<UserPreferences> loadUserPreferences() throws IOException {
        def settingsPath = Paths.get(settingsFilename)
        try {
            settingsPath.withInputStream {
                return Optional.of(yaml.loadAs(it, UserPreferences))
            } as Optional<UserPreferences>
        } catch (NoSuchFileException ex) {
            log.warn("Konnte Datei unter ${settingsPath.toAbsolutePath()} nicht finden", ex)
            return Optional.empty()
        }
    }

    @Override
    void saveUserPreferences(@NonNull UserPreferences userPreferences) throws IOException {
        Paths.get settingsFilename withWriter StandardCharsets.UTF_8.name(), {
            yaml.dump userPreferences, it
        }
    }
}
