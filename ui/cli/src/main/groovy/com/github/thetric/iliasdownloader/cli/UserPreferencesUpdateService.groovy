package com.github.thetric.iliasdownloader.cli

/**
 * Updates the {@link com.github.thetric.iliasdownloader.ui.common.prefs.UserPreferences}.
 */
interface UserPreferencesUpdateService {
    SyncSettings updatePreferences(final CliOptions cliOptions)
}
