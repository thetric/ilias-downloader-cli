package com.github.thetric.iliasdownloader.ui.common.prefs;

import lombok.Data;

import java.util.Set;

/**
 * @author broj
 * @since 16.01.2017
 */
@Data
public final class UserPreferences {
    private String iliasServerURL;

    private String userName;

    private long maxFileSize;

    private Set<Long> activeCourses;
}
