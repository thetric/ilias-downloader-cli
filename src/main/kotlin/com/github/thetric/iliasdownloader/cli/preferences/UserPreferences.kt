package com.github.thetric.iliasdownloader.cli.preferences

import kotlinx.serialization.Serializable

/**
 * Contains the user preferences.
 *
 * @see JsonPreferenceService
 */
@Serializable
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
