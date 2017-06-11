package com.github.thetric.iliasdownloader.cli

import com.github.thetric.iliasdownloader.service.model.Course
import groovy.transform.Immutable

/**
 * Temporary sync settings.
 * The settings are persisted as {@link com.github.thetric.iliasdownloader.ui.common.prefs.UserPreferences}.
 */
@Immutable
final class SyncSettings {
    final Collection<Course> courses
    final long maxFileSizePerFileInMiB
}
