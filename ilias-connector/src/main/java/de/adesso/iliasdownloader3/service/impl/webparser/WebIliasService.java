package de.adesso.iliasdownloader3.service.impl.webparser;

import de.adesso.iliasdownloader3.service.IliasService;
import de.adesso.iliasdownloader3.service.exception.IliasAuthenticationException;
import de.adesso.iliasdownloader3.service.model.Course;
import de.adesso.iliasdownloader3.service.model.LoginCredentials;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author broj
 * @since 31.05.2016
 */
@RequiredArgsConstructor
public final class WebIliasService implements IliasService {
    private static final String ILIAS_BASE_URL = "https://www.ilias.fh-dortmund.de/ilias/";
    // TODO externalize base URL
    private static final String LOGIN_PAGE = ILIAS_BASE_URL + "login.php";
    private static final String LOGOUT_PAGE = ILIAS_BASE_URL + "logout.php";
    private static final String COURSES_AND_GROUPS_OVERVIEW = ILIAS_BASE_URL + "ilias.php" +
            "?baseClass=ilPersonalDesktopGUI&cmd=jumpToSelectedItems";

    @NonNull
    private final WebIoExceptionTranslator exceptionTranslator;
    @NonNull
    private Map<String, String> cookies = new HashMap<>();

    @Override
    public void login(LoginCredentials loginCredentials) {
        Response response;
        try {
            response = Jsoup
                    .connect(LOGIN_PAGE)
                    .data("username", loginCredentials.getUserName())
                    .data("password", loginCredentials.getPassword())
                    .method(Connection.Method.POST)
                    .execute();
        } catch (IOException e) {
            throw exceptionTranslator.translate(e);
        }

        ensureAuthentication(response);
        cookies = response.cookies();
    }

    private void ensureAuthentication(Response response) {
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
        cookies.clear();
        // hat den Effekt, dass das Cookie "ilClientId" gelöscht wird
        // wenn wieder der Login aufgerufen wird, hat der Client noch das "authchallenge" Cookie
        // ist der Logout noch nötig?
        try {
            Response response = connectWithSessionCookies(LOGOUT_PAGE)
                    .method(Connection.Method.GET)
                    .execute();
        } catch (IOException e) {
            throw exceptionTranslator.translate(e);
        }
    }

    private Connection connectWithSessionCookies(String iliasWebsite) {
        return Jsoup
                .connect(iliasWebsite)
                .cookies(cookies);
    }

    @Override
    public Collection<Course> getJoinedCourses() {
        Response response;
        Document document;

        try {
            response = connectWithSessionCookies(COURSES_AND_GROUPS_OVERVIEW)
                    .execute();
            document = response.parse();
        } catch (IOException e) {
            throw exceptionTranslator.translate(e);
        }

        String courseLinkCssSelector = "a[href*='_crs_'].il_ContainerItemTitle";
        Elements elements = document.select(courseLinkCssSelector);

        return elements.stream().map(aTag -> {
            int courseId = getCourseId(aTag);
            String courseName = aTag.text();
            return new Course(courseId, courseName);
        }).collect(Collectors.toList());
    }

    private int getCourseId(Element aTag) {
        String href = aTag.attr("href");
        // href="http://www.ilias.fh-dortmund.de/ilias/goto_ilias-fhdo_crs_XXXX.html"
        String idString = href
                .replaceFirst("http://www.ilias.fh-dortmund.de/ilias/goto_ilias-fhdo_crs_", "")
                .replace(".html", "");
        // der Rest muss ein int sein
        return Integer.parseInt(idString);
    }
}
