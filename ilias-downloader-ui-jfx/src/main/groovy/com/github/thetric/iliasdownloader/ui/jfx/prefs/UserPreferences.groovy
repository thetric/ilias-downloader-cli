package com.github.thetric.iliasdownloader.ui.jfx.prefs

import groovy.transform.CompileStatic
/**
 * Created by Dominik Broj on 31.01.2016.
 *
 * @author Dominik Broj
 * @since 31.01.2016
 */
@CompileStatic
final class UserPreferences {
    String iliasServerURL

    String userName

    String downloadFolder

    long maxFileSize

    Set<Long> activeCourses
}
