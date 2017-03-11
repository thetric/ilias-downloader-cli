package com.github.thetric.iliasdownloader.service.webparser

import com.github.thetric.iliasdownloader.service.IliasService
import com.github.thetric.iliasdownloader.service.IliasServiceProvider
import com.github.thetric.iliasdownloader.service.webparser.impl.NoCookiesAvailableException
import com.github.thetric.iliasdownloader.service.webparser.impl.WebIliasService
import com.github.thetric.iliasdownloader.service.webparser.impl.course.CourseSyncServiceImpl
import com.github.thetric.iliasdownloader.service.webparser.impl.course.jsoup.JSoupParserServiceImpl
import com.github.thetric.iliasdownloader.service.webparser.impl.util.WebIoExceptionTranslatorImpl
import com.github.thetric.iliasdownloader.service.webparser.impl.util.fluenthc.FluentHcExecutorFactoryImpl
import groovy.transform.CompileStatic
import org.jsoup.Jsoup

import static org.jsoup.Connection.Response

@CompileStatic
final class WebParserIliasServiceProvider implements IliasServiceProvider {
    private static final String LOGIN_PAGE_NAME = 'login.php'
    private static final String ILIAS_CLIENT_ID_COOKIE_NAME = 'ilClientId'

    private final String iliasBaseUrl
    private final String clientId

    WebParserIliasServiceProvider(String loginPage) throws IOException {
        iliasBaseUrl = getBaseUrl(loginPage)
        clientId = getClientId(loginPage)
    }

    private String getBaseUrl(String loginPage) {
        String trimmed = loginPage.trim()
        if (trimmed.empty) {
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
        String id
        try {
            Response response = Jsoup.connect(loginPage).execute()
            id = response.cookie(ILIAS_CLIENT_ID_COOKIE_NAME)
        } catch (IOException e) {
            throw new IOException("Konnte die URL '$loginPage' nicht erreichen", e)
        }
        return Optional.ofNullable(id).orElseThrow {
            new NoCookiesAvailableException(
                "Konnte das Cookie '${WebParserIliasServiceProvider.ILIAS_CLIENT_ID_COOKIE_NAME}' nicht in der Response " +
                    "von der Seite $loginPage finden")
        }
    }

    @Override
    IliasService newInstance() {
        def webIoExceptionTranslator = new WebIoExceptionTranslatorImpl()
        def jSoupParserService = new JSoupParserServiceImpl()
        def fluentHcExecutorProvider = new FluentHcExecutorFactoryImpl()
        def courseSyncServiceProvider = {
            new CourseSyncServiceImpl(
                webIoExceptionTranslator,
                jSoupParserService,
                iliasBaseUrl,
                clientId)
        }
        return new WebIliasService(
            webIoExceptionTranslator,
            iliasBaseUrl, clientId,
            fluentHcExecutorProvider,
            courseSyncServiceProvider)
    }
}
