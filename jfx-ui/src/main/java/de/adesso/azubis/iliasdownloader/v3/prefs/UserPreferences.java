package de.adesso.azubis.iliasdownloader.v3.prefs;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import de.adesso.iliasdownloader3.service.impl.soap.model.DownloadMethod;
import de.adesso.iliasdownloader3.service.impl.soap.model.LoginType;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

import static de.adesso.iliasdownloader3.service.impl.soap.model.DownloadMethod.WEBSERVICE;
import static de.adesso.iliasdownloader3.service.impl.soap.model.LoginType.LDAP;
import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.SECONDS;

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

    @JacksonXmlProperty(localName = "iliasclient")
    private String iliasClient = "";

    @JacksonXmlProperty(localName = "userlogin")
    private String userName = "";

    @JacksonXmlProperty(localName = "basedir")
    private String baseDirectory = "ilias";

    @JacksonXmlProperty(localName = "maxsize")
    private long maxFileSize = Long.MAX_VALUE;

    @JacksonXmlProperty(localName = "logintype")
    private LoginType loginType = LDAP;

    @JacksonXmlProperty(localName = "downloadmethod")
    private DownloadMethod downloadMethod = WEBSERVICE;

    @JacksonXmlProperty(localName = "allowdownload")
    private boolean allowDownload = true;

    @JacksonXmlProperty(localName = "autosyncactive")
    private boolean autoSyncActive = false;

    @JacksonXmlProperty(localName = "autosyncinterval")
    private long autoSyncIntervalInSeconds = HOURS.convert(1L, SECONDS);

    @JacksonXmlProperty(localName = "syncallcourses")
    private boolean syncAll = true;

    @JacksonXmlProperty(localName = "activecourses")
    private Set<Long> activeCourses = new HashSet<>();

    @JacksonXmlProperty(localName = "blockedfiles")
    private Set<Long> blockedFiles = new HashSet<>();
}
