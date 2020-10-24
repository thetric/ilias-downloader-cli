package com.github.thetric.iliasdownloader.cli

import com.github.thetric.iliasdownloader.cli.preferences.UserPreferences
import com.github.thetric.iliasdownloader.connector.model.Course

/**
 * Temporary sync settings. The settings are persisted as [UserPreferences].
 */
internal data class SyncSettings(
    val courses: Collection<Course> = emptyList(),
    val maxFileSizePerFileInMiB: Long = 0
)
