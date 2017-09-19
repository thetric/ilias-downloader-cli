package com.github.thetric.iliasdownloader.cli

import com.github.thetric.iliasdownloader.ui.common.prefs.UserPreferences

/**
 * Updates the [UserPreferences].
 */
internal interface UserPreferencesUpdateService {
    fun updatePreferences(cliOptions: CliOptions): SyncSettings
}
