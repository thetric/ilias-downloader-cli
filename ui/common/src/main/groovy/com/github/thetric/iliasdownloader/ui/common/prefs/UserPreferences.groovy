package com.github.thetric.iliasdownloader.ui.common.prefs

import groovy.transform.Canonical

/**
 * Contains the user preferences.
 *
 * @see UserPreferenceService
 * @see JsonUserPreferenceService
 */
@Canonical
final class UserPreferences {
    String iliasServerURL

    String userName

    /**
     * Limit per download in mebibytes. Zero means unlimited
     */
    long maxFileSizeInMiB

    /**
     * Course IDs to include in the sync
     */
    Collection<Long> activeCourses = new HashSet<>()
}
