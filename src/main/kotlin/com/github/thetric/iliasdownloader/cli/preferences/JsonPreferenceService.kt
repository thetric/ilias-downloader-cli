package com.github.thetric.iliasdownloader.cli.preferences

import com.google.gson.GsonBuilder
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

/**
 * Saves and loads [UserPreferences] as JSON
 */
class JsonPreferenceService(val settingsFile: Path) {
    private val gson = GsonBuilder().setPrettyPrinting().create()

    fun loadPreferences(): UserPreferences {
        Files.newBufferedReader(settingsFile, StandardCharsets.UTF_8).use {
            return gson.fromJson(it, UserPreferences::class.java)
        }
    }

    fun savePreferences(userPreferences: UserPreferences) {
        val json = gson.toJson(userPreferences)
        Files.createDirectories(settingsFile.parent)
        Files.newBufferedWriter(settingsFile, StandardCharsets.UTF_8).use {
            it.write(json)
        }
    }
}

