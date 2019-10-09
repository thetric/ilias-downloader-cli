package com.github.thetric.iliasdownloader.cli

import com.github.thetric.iliasdownloader.connector.api.model.Course
import com.github.thetric.iliasdownloader.ui.common.prefs.UserPreferences

/**
 * Temporary sync settings. The settings are persisted as [UserPreferences].
 */
internal data class SyncSettings(
    val courses: Collection<Course> = emptyList(),
    val maxFileSizePerFileInMiB: Long = 0
)
