package com.github.thetric.iliasdownloader.ui.jfx.prefs

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
/**
 * Created by Dominik Broj on 31.01.2016.
 *
 * @author Dominik Broj
 * @since 31.01.2016
 */
final class UserPreferences {
    @JacksonXmlProperty(localName = 'server')
    String iliasServerURL = ''

    @JacksonXmlProperty(localName = 'userlogin')
    String userName = ''

    @JacksonXmlProperty(localName = 'basedir')
    String baseDirectory = 'ilias'

    @JacksonXmlProperty(localName = 'maxsize')
    long maxFileSize = Long.MAX_VALUE

    @JacksonXmlProperty(localName = 'activecourses')
    Set<Long> activeCourses = new HashSet<>()

    @JacksonXmlProperty(localName = 'blockedfiles')
    Set<Long> blockedFiles = new HashSet<>()
}
