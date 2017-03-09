package com.github.thetric.iliasdownloader.ui.common.prefs;

import lombok.Data;

import java.util.Collection;
import java.util.HashSet;

@Data
public final class UserPreferences {
    private String iliasServerURL;

    private String userName;

    private long maxFileSize;

    private Collection<Long> activeCourses = new HashSet<>();
}
