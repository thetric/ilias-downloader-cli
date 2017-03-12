package com.github.thetric.iliasdownloader.ui.common.prefs

import groovy.transform.CompileStatic
import org.yaml.snakeyaml.Yaml

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

/**
 * Loads and saves {@link UserPreferences} from and to a {@code .yml} file.
 */
@CompileStatic
final class YamlUserPreferenceService implements UserPreferenceService {
    private final Yaml yaml
    final Path settingsFile

    YamlUserPreferenceService(Path settingsPath) {
        this.settingsFile = settingsPath
        yaml = new Yaml()
    }

    UserPreferences loadUserPreferences() throws IOException {
        return settingsFile.withInputStream {
            return yaml.loadAs(it, UserPreferences)
        } as UserPreferences
    }

    @Override
    void saveUserPreferences(UserPreferences userPreferences) throws IOException {
        if (Files.notExists(settingsFile)) {
            if (Files.notExists(settingsFile.parent)) {
                Files.createDirectories(settingsFile.parent)
            }
            Files.createFile settingsFile
        }
        settingsFile.withWriter StandardCharsets.UTF_8.name(), {
            yaml.dump userPreferences, it
        }
    }
}
