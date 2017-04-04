package com.github.thetric.iliasdownloader.service.webparser

import com.github.thetric.iliasdownloader.service.IliasService
import com.github.thetric.iliasdownloader.service.IliasServiceProvider
import com.github.thetric.iliasdownloader.service.webparser.impl.NoCookiesAvailableException
import com.github.thetric.iliasdownloader.service.webparser.impl.WebIliasService
import com.github.thetric.iliasdownloader.service.webparser.impl.course.CourseSyncService
import com.github.thetric.iliasdownloader.service.webparser.impl.course.CourseSyncServiceImpl
import com.github.thetric.iliasdownloader.service.webparser.impl.course.jsoup.JSoupParserService
import com.github.thetric.iliasdownloader.service.webparser.impl.course.jsoup.JSoupParserServiceImpl
import com.github.thetric.iliasdownloader.service.webparser.impl.util.WebIoExceptionTranslator
import com.github.thetric.iliasdownloader.service.webparser.impl.util.WebIoExceptionTranslatorImpl
import com.github.thetric.iliasdownloader.service.webparser.impl.util.fluenthc.FluentHcExecutorFactory
import com.github.thetric.iliasdownloader.service.webparser.impl.util.fluenthc.FluentHcExecutorFactoryImpl
import groovy.transform.CompileStatic

@CompileStatic
final class WebParserIliasServiceProvider implements IliasServiceProvider {
    private static final String LOGIN_PAGE_NAME = 'login.php'
    private static final String ILIAS_CLIENT_ID_COOKIE_NAME = 'ilClientId'

    private final CookieService cookieService

    private final String iliasBaseUrl
    private final String clientId

    WebParserIliasServiceProvider(CookieService cookieService, String loginPage) throws IOException {
        this.cookieService = cookieService
        iliasBaseUrl = getBaseUrl(loginPage)
        clientId = getClientId(loginPage)
    }

    private String getBaseUrl(String loginPage) {
        String trimmed = loginPage?.trim()
        if (!trimmed) {
            throw new IllegalArgumentException('Die angegebene Loginseiten URL darf nicht leer sein')
        }
        if (!trimmed.startsWith('http://') && !trimmed.startsWith('https://')) {
            trimmed = "https://$loginPage"
        }
        int loginPageNameIndex = trimmed.indexOf(LOGIN_PAGE_NAME)
        if (loginPageNameIndex == -1) {
            throw new IllegalArgumentException("Die angegebene URL enthält kein '$LOGIN_PAGE_NAME'")
        }
        return loginPage[0..loginPageNameIndex - 1]
    }

    private String getClientId(String loginPage) throws IOException {
        final String id
        try {
            id = cookieService.getCookieFromUrl(loginPage, ILIAS_CLIENT_ID_COOKIE_NAME)
        } catch (IOException e) {
            throw new IOException("Konnte die URL '$loginPage' nicht erreichen", e)
        }
        return Optional.ofNullable(id).orElseThrow({
            throw new NoCookiesAvailableException("Konnte das Cookie '" + ILIAS_CLIENT_ID_COOKIE_NAME +
                "' nicht in der Response von der Seite $loginPage finden")
        })
    }

    @Override
    IliasService newInstance() {
        WebIoExceptionTranslator webIoExceptionTranslator = new WebIoExceptionTranslatorImpl()
        JSoupParserService jSoupParserService = new JSoupParserServiceImpl()
        FluentHcExecutorFactory fluentHcExecutorProvider = new FluentHcExecutorFactoryImpl()
        CourseSyncService courseSyncServiceProvider = new CourseSyncServiceImpl(
                webIoExceptionTranslator,
                jSoupParserService,
                iliasBaseUrl,
                clientId)
        return new WebIliasService(
            webIoExceptionTranslator,
            iliasBaseUrl, clientId,
            fluentHcExecutorProvider,
            courseSyncServiceProvider)
    }
}
