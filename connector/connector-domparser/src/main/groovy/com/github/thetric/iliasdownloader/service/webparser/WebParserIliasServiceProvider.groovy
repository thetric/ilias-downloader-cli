package com.github.thetric.iliasdownloader.service.webparser

import com.github.thetric.iliasdownloader.service.IliasService
import com.github.thetric.iliasdownloader.service.IliasServiceProvider
import com.github.thetric.iliasdownloader.service.webparser.impl.CookieNotFoundException
import com.github.thetric.iliasdownloader.service.webparser.impl.WebIliasService
import com.github.thetric.iliasdownloader.service.webparser.impl.course.CourseSyncService
import com.github.thetric.iliasdownloader.service.webparser.impl.course.CourseSyncServiceImpl
import com.github.thetric.iliasdownloader.service.webparser.impl.course.jsoup.JSoupParserService
import com.github.thetric.iliasdownloader.service.webparser.impl.course.jsoup.JSoupParserServiceImpl
import com.github.thetric.iliasdownloader.service.webparser.impl.webclient.IliasWebClient
import com.github.thetric.iliasdownloader.service.webparser.impl.webclient.OkHttpIliasWebClient
import groovy.transform.CompileStatic

/**
 * Provides an {@link IliasService} that retrieves information from the Ilias service by parsing the HTML pages.
 */
@CompileStatic
final class WebParserIliasServiceProvider implements IliasServiceProvider {
    private static final String LOGIN_PAGE_NAME = 'login.php'
    private static final String ILIAS_CLIENT_ID_COOKIE_NAME = 'ilClientId'

    private final CookieService cookieService

    final String iliasBaseUrl
    final String clientId

    WebParserIliasServiceProvider(final CookieService cookieService, final String loginPage) throws IOException {
        this.cookieService = cookieService
        iliasBaseUrl = retrieveBaseUrl(loginPage)
        clientId = retrieveClientId(loginPage)
    }

    private String retrieveBaseUrl(final String loginPage) {
        String trimmed = loginPage?.trim()
        if (!trimmed) {
            throw new IllegalArgumentException('Die angegebene Loginseiten URL darf nicht leer sein')
        }
        if (!trimmed.startsWith('http://') && !trimmed.startsWith('https://')) {
            trimmed = "https://$loginPage"
        }
        final int loginPageNameIndex = trimmed.indexOf(LOGIN_PAGE_NAME)
        if (loginPageNameIndex == -1) {
            throw new IllegalArgumentException("Die angegebene URL enthält kein '$LOGIN_PAGE_NAME'")
        }
        return trimmed[0..loginPageNameIndex - 1]
    }

    private String retrieveClientId(final String loginPage) throws IOException {
        final String id
        try {
            id = cookieService.getCookieFromUrl(loginPage, ILIAS_CLIENT_ID_COOKIE_NAME)
        } catch (final IOException e) {
            throw new IOException("Konnte die URL '$loginPage' nicht erreichen", e)
        }
        return Optional.ofNullable(id).orElseThrow {
            throw new CookieNotFoundException("Konnte das Cookie '" + ILIAS_CLIENT_ID_COOKIE_NAME +
                "' nicht in der Response von der Seite $loginPage finden")
        }
    }

    @Override
    IliasService newInstance() {
        final JSoupParserService jSoupParserService = new JSoupParserServiceImpl()
        final IliasWebClient iliasWebClient = new OkHttpIliasWebClient(iliasBaseUrl, clientId)
        final CourseSyncService courseSyncServiceProvider = new CourseSyncServiceImpl(
            jSoupParserService,
            iliasWebClient,
            iliasBaseUrl,
            clientId)
        return new WebIliasService(courseSyncServiceProvider, iliasWebClient)
    }
}
