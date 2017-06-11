package com.github.thetric.iliasdownloader.ui.common.prefs

import groovy.json.JsonOutput
import groovy.json.JsonSlurper

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

/**
 * Saves and loads {@link UserPreferences} as JSON
 */
final class JsonUserPreferenceService implements UserPreferenceService {
    private final JsonSlurper jsonSlurper
    final Path settingsFile

    JsonUserPreferenceService(final Path settingsPath) {
        this.settingsFile = settingsPath
        jsonSlurper = new JsonSlurper()
    }

    @Override
    UserPreferences loadUserPreferences() throws IOException {
        return settingsFile.withInputStream { new UserPreferences(jsonSlurper.parse(it) as Map) } as UserPreferences
    }

    @Override
    void saveUserPreferences(final UserPreferences userPreferences) throws IOException {
        Files.createDirectories(settingsFile.parent)
        settingsFile.withWriter StandardCharsets.UTF_8.name(), {
            final String compressedJson = JsonOutput.toJson(userPreferences)
            final String prettyPrintedJson = JsonOutput.prettyPrint(compressedJson)
            it.write("$prettyPrintedJson\n")
        }
    }
}
