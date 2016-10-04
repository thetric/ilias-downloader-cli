package com.github.thetric.iliasdownloader.ui.jfx.prefs;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Dominik Broj on 31.01.2016.
 *
 * @author Dominik Broj
 * @since 31.01.2016
 */
@Data
public final class UserPreferences {
    @JacksonXmlProperty(localName = "server")
    private String iliasServerURL = "";

    @JacksonXmlProperty(localName = "userlogin")
    private String userName = "";

    @JacksonXmlProperty(localName = "basedir")
    private String baseDirectory = "ilias";

    @JacksonXmlProperty(localName = "maxsize")
    private long maxFileSize = Long.MAX_VALUE;

    @JacksonXmlProperty(localName = "activecourses")
    private Set<Long> activeCourses = new HashSet<>();

    @JacksonXmlProperty(localName = "blockedfiles")
    private Set<Long> blockedFiles = new HashSet<>();
}
