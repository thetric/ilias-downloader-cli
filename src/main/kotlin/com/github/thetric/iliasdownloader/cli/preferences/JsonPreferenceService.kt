package com.github.thetric.iliasdownloader.cli.preferences

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

/**
 * Saves and loads [UserPreferences] as JSON
 */
class JsonPreferenceService(val settingsFile: Path) {
    private val json = Json { prettyPrint = true }

    fun loadPreferences(): UserPreferences {
        val jsonContent = Files.newBufferedReader(settingsFile, StandardCharsets.UTF_8).readText()
        return json.decodeFromString(jsonContent)
    }

    fun savePreferences(userPreferences: UserPreferences) {
        val jsonString = json.encodeToString(userPreferences)
        Files.createDirectories(settingsFile.parent)
        Files.newBufferedWriter(settingsFile, StandardCharsets.UTF_8).use {
            it.write(jsonString)
        }
    }
}

