package com.github.thetric.iliasdownloader.ui.common.prefs

/**
 * Contains the user preferences.
 *
 * @see PreferenceService
 * @see JsonPreferenceService
 */
data class UserPreferences(
    var iliasServerURL: String,
    val userName: String,

    /**
     * Limit per download in mebibytes. Zero means unlimited
     */
    val maxFileSizeInMiB: Long,

    /**
     * Course IDs to include in the sync
     */
    val activeCourses: Collection<Long> = HashSet()
)
