package com.github.thetric.iliasdownloader.ui.common.prefs;

import lombok.Data;

import java.util.Collection;
import java.util.HashSet;

/**
 * Contains the user preferences.
 *
 * @see UserPreferenceService
 * @see YamlUserPreferenceService
 */
@Data
public final class UserPreferences {
    private String iliasServerURL;

    private String userName;

    /**
     * Limit per download in mebibytes. Zero means unlimited
     */
    private long maxFileSizeInMiB;

    /**
     * Course IDs to include in the sync
     */
    private Collection<Long> activeCourses = new HashSet<>();
}
