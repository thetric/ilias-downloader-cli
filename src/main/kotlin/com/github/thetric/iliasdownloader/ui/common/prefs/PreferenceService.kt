package com.github.thetric.iliasdownloader.ui.common.prefs

import java.io.IOException
import java.nio.file.Path

/**
 * Saves and loads [PreferenceService]s.
 */
interface PreferenceService<T> {
    /**
     * Loads the [UserPreferences].
     *
     * @return the [UserPreferences]
     * @throws IOException
     * when the [UserPreferences] cannot be loaded
     */
    @Throws(IOException::class)
    fun loadPreferences(): T

    /**
     * Saves the [UserPreferences].
     *
     * @param userPreferences
     * [UserPreferences] to save
     * @throws IOException
     */
    @Throws(IOException::class)
    fun savePreferences(userPreferences: T)

    val settingsFile: Path
}
