package com.github.thetric.iliasdownloader.service.webparser;

import com.github.thetric.iliasdownloader.service.IliasService;
import com.github.thetric.iliasdownloader.service.IliasServiceProvider;
import lombok.NonNull;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.Optional;

import static org.jsoup.Connection.Response;

/**
 * @author broj
 * @since 25.09.2016
 */
public final class WebParserIliasServiceProvider implements IliasServiceProvider {
    private static final String LOGIN_PAGE_NAME = "login.php";
    private static final String ILIAS_CLIENT_ID_COOKIE_NAME = "ilClientId";

    private final String iliasBaseUrl;
    private final String clientId;

    public WebParserIliasServiceProvider(@NonNull String loginPage) throws IOException {
        iliasBaseUrl = getBaseUrl(loginPage);
        clientId = getClientId(loginPage);
    }

    private String getBaseUrl(String loginPage) {
        loginPage = loginPage.trim();
        if (loginPage.isEmpty()) {
            throw new IllegalArgumentException("Die angegebene Loginseiten URL darf nicht leer sein");
        }
        if (!loginPage.startsWith("http://") && !loginPage.startsWith("https://")) {
            loginPage = "https://" + loginPage;
        }
        int loginPageNameIndex = loginPage.indexOf(LOGIN_PAGE_NAME);
        if (loginPageNameIndex == -1) {
            throw new IllegalArgumentException("Die angegebene URL enthält kein '" + LOGIN_PAGE_NAME + "'");
        }
        return loginPage.substring(0, loginPageNameIndex);
    }

    private String getClientId(String loginPage) throws IOException {
        String id;
        try {
            Response response = Jsoup.connect(loginPage).execute();
            id = response.cookie(ILIAS_CLIENT_ID_COOKIE_NAME);
        } catch (IOException e) {
            throw new IOException("Konnte die URL '" + loginPage + "' nicht erreichen", e);
        }
        return Optional.ofNullable(id).orElseThrow(() -> new NoCookiesAvailableException(String.format(
                "Konnte das Cookie '%s' nicht in der Response von der Seite %s finden",
                ILIAS_CLIENT_ID_COOKIE_NAME,
                loginPage))
        );
    }

    @Override
    public IliasService newInstance() {
        return new WebIliasService(new WebIoExceptionTranslatorImpl(), iliasBaseUrl, clientId);
    }
}
