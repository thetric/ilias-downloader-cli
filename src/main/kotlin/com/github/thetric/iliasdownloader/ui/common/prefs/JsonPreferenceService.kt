package com.github.thetric.iliasdownloader.ui.common.prefs

import com.google.gson.GsonBuilder
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

/**
 * Saves and loads [UserPreferences] as JSON
 */
class JsonPreferenceService<T>(
    override val settingsFile: Path,
    private val classType: Class<T>
) : PreferenceService<T> {
    private val gson = GsonBuilder().setPrettyPrinting().create()

    @Throws(IOException::class)
    override fun loadPreferences(): T {
        Files.newBufferedReader(settingsFile, StandardCharsets.UTF_8).use {
            return gson.fromJson(it, classType)
        }
    }

    @Throws(IOException::class)
    override fun savePreferences(userPreferences: T) {
        val json = gson.toJson(userPreferences)
        Files.createDirectories(settingsFile.parent)
        Files.newBufferedWriter(settingsFile, StandardCharsets.UTF_8).use {
            it.write(json)
        }
    }
}

