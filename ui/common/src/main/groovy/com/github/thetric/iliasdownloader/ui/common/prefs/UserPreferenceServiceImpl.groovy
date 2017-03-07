package com.github.thetric.iliasdownloader.ui.common.prefs

import groovy.transform.CompileStatic
import groovy.util.logging.Log4j2
import org.yaml.snakeyaml.Yaml

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
/**
 * Created by Dominik Broj on 31.01.2016.
 *
 * @author Dominik Broj
 * @since 31.01.2016
 */
@CompileStatic
@Log4j2
final class UserPreferenceServiceImpl implements UserPreferenceService {
    private final Yaml yaml
    final Path settingsFile

    UserPreferenceServiceImpl(Path settingsPath) {
        this.settingsFile = settingsPath
        yaml = new Yaml()
    }

    UserPreferences loadUserPreferences() throws IOException {
        return settingsFile.withInputStream {
            return yaml.loadAs(it, UserPreferences.class)
        } as UserPreferences
    }

    @Override
    void saveUserPreferences(UserPreferences userPreferences) throws IOException {
        if (Files.notExists(settingsFile)) {
            Files.createDirectories(settingsFile.parent)
            Files.createFile settingsFile
        }
        settingsFile.withWriter StandardCharsets.UTF_8.name(), {
            yaml.dump userPreferences, it
        }
    }
}
