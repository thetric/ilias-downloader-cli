package de.adesso.iliasdownloader3.service.impl.webparser;

import de.adesso.iliasdownloader3.service.IliasService;
import de.adesso.iliasdownloader3.service.exception.IliasAuthenticationException;
import de.adesso.iliasdownloader3.service.model.Course;
import de.adesso.iliasdownloader3.service.model.CourseItem;
import de.adesso.iliasdownloader3.service.model.LoginCredentials;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
@Log4j2
public final class WebIliasService implements IliasService {
    private static final String ILIAS_BASE_URL = "https://www.ilias.fh-dortmund.de/ilias/";
    // TODO externalize base URL
    private static final String LOGIN_PAGE = ILIAS_BASE_URL + "login.php";
    private static final String LOGOUT_PAGE = ILIAS_BASE_URL + "logout.php";
    private static final String COURSES_AND_GROUPS_OVERVIEW = ILIAS_BASE_URL + "ilias.php" +
            "?baseClass=ilPersonalDesktopGUI&cmd=jumpToSelectedItems";

    private static final String ILIAS_CSS_SELECTOR_COURSE = "a[href*='_crs_'].il_ContainerItemTitle";

    @NonNull
    private final WebIoExceptionTranslator exceptionTranslator;
    @NonNull
    private Map<String, String> cookies = new HashMap<>();

    @Override
    public void login(@NonNull LoginCredentials loginCredentials) {
        Response response;
        log.info("Logging in at {}", LOGIN_PAGE);
        try {
            response = Jsoup
                    .connect(LOGIN_PAGE)
                    .data("username", loginCredentials.getUserName())
                    .data("password", loginCredentials.getPassword())
                    .method(Connection.Method.POST)
                    .execute();
        } catch (IOException e) {
            log.error("Login at " + LOGIN_PAGE + " failed", e);
            throw exceptionTranslator.translate(e);
        }
        log.info("Login at {} succeeded", LOGIN_PAGE);

        ensureAuthentication(response);
        cookies = response.cookies();
    }

    private void ensureAuthentication(@NonNull Response response) {
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
        log.info("Logging out: {}", LOGOUT_PAGE);

        try {
            Response response = connectWithSessionCookies(LOGOUT_PAGE)
                    .method(Connection.Method.GET)
                    .execute();
        } catch (IOException e) {
            log.error("Logout at " + LOGOUT_PAGE + " failed", e);
            throw exceptionTranslator.translate(e);
        }
        log.info("Logout at {} succeeded", LOGOUT_PAGE);
    }

    private Connection connectWithSessionCookies(@NonNull String iliasWebsite) {
        if (cookies.isEmpty()) {
            throw new NoCookiesAvailableException("No cookies found. Ensure you are already logged in");
        }
        return Jsoup
                .connect(iliasWebsite)
                .cookies(cookies);
    }

    private Document connectAndGetDocument(@NonNull String url) {
        try {
            Response response = connectWithSessionCookies(url).execute();
            return response.parse();
        } catch (IOException e) {
            log.error("Could not GET: " + url, e);
            throw exceptionTranslator.translate(e);
        }
    }

    @Override
    public Collection<Course> getJoinedCourses() {
        log.info("Get all courses and groups from {}", COURSES_AND_GROUPS_OVERVIEW);
        Document document = connectAndGetDocument(COURSES_AND_GROUPS_OVERVIEW);

        log.info("Mapping course/groups to entities...");
        Collection<Course> courses = getCoursesFromHtml(document);
        log.debug("Found courses: {}", courses);
        return courses;
    }

    private Collection<Course> getCoursesFromHtml(@NonNull Document document) {
        Elements elements = document.select(ILIAS_CSS_SELECTOR_COURSE);

        return elements.stream().map(aTag -> {
            int courseId = getCourseId(aTag);
            String courseName = aTag.text();
            String courseUrl = aTag.attr("href");
            return new Course(courseId, courseName, courseUrl);
        }).collect(Collectors.toList());
    }

    private int getCourseId(@NonNull Element aTag) {
        String href = aTag.attr("href");
        // href="http://www.ilias.fh-dortmund.de/ilias/goto_ilias-fhdo_crs_\d+.html"
        String idString = href
                .replaceFirst(ILIAS_BASE_URL + "goto_ilias-fhdo_crs_", "")
                .replace(".html", "");
        // der Rest muss ein int sein
        return Integer.parseInt(idString);
    }

    @Override
    public Collection<Course> searchCoursesWithContent(@NonNull Collection<Course> selectedCourses) {
        return null;
    }

    private Course findCourseChildNodes(@NonNull Course course) {
        Collection<? extends CourseItem> childNodes = searchContentRecursively(course);
        return new Course(course.getId(), course.getName(), course.getUrl(), childNodes);
    }

    private Collection<? extends CourseItem> searchContentRecursively(Course course) {
        return null;
    }
}
