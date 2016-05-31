package de.adesso.iliasdownloader3.service.impl.webparser;

import de.adesso.iliasdownloader3.service.IliasService;
import de.adesso.iliasdownloader3.service.exception.IliasAuthenticationException;
import de.adesso.iliasdownloader3.service.model.LoginCredentials;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

/**
 * @author broj
 * @since 31.05.2016
 */
@RequiredArgsConstructor
public final class WebIliasService implements IliasService {
    // TODO externalisieren
    private static final String ILIAS_LOGIN_PAGE = "https://www.ilias.fh-dortmund.de/ilias/login.php";
    private static final String ILIAS_LOGOUT_PAGE = "http://www.ilias.fh-dortmund.de/ilias/logout.php";

    @NonNull
    private final WebIoExceptionTranslator exceptionTranslator;

    @Override
    public void login(LoginCredentials loginCredentials) {
        Connection.Response response;
        try {
            response = Jsoup
                    .connect(ILIAS_LOGIN_PAGE)
                    .data("username", loginCredentials.getUserName())
                    .data("password", loginCredentials.getPassword())
                    .method(Connection.Method.POST)
                    .execute();
        } catch (IOException e) {
            throw exceptionTranslator.translate(e);
        }

        // diese Prüfung ist nicht 100% wasserdicht. Das Cookie kann trotzdem gesetzt worden sein, wenn sich der Nutzer
        // zuvor erfolgreich ein- und wieder ausgeloggt hat und sich dann der Login fehlschlägt
        // es wäre vielleicht sicherer, die URL zu prüfen
        boolean isAuthenticated = response.cookies().containsKey("authchallenge");
        if (!isAuthenticated) {
            // bessere Fehlermeldung?
            throw new IliasAuthenticationException("Login fehlgeschlagen");
        }
    }

    @Override
    public void logout() {
        // hat den Effekt, dass das Cookie "ilClientId" gelöscht wird
        // wenn wieder der Login aufgerufen wird, hat der Client noch das "authchallenge" Cookie
        // ist der Logout noch nötig?
        try {
            Connection.Response response = Jsoup
                    .connect(ILIAS_LOGOUT_PAGE)
                    .method(Connection.Method.GET)
                    .execute();
        } catch (IOException e) {
            throw exceptionTranslator.translate(e);
        }
    }
}
