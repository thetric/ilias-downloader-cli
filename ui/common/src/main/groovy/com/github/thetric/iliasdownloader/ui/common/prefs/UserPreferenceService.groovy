package com.github.thetric.iliasdownloader.ui.common.prefs

import groovy.transform.CompileStatic

/**
 * Saves and loads {@link UserPreferenceService}s.
 */
@CompileStatic
interface UserPreferenceService {
    /**
     * Loads the {@link UserPreferences}.
     *
     * @return the {@link UserPreferences}
     * @throws IOException when the {@link UserPreferences} cannot be loaded
     */
    UserPreferences loadUserPreferences() throws IOException

    /**
     * Saves the {@link UserPreferences}.
     * @param userPreferences {@link UserPreferences} to save
     * @throws IOException
     */
    void saveUserPreferences(UserPreferences userPreferences) throws IOException
}
