package de.adesso.iliasdownloader3.service.impl.webparser;

import de.adesso.iliasdownloader3.service.IliasService;
import de.adesso.iliasdownloader3.service.exception.IliasAuthenticationException;
import de.adesso.iliasdownloader3.service.model.*;
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
import java.util.*;
import java.util.regex.Pattern;

import static de.adesso.iliasdownloader3.service.impl.webparser.CssSelectors.*;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

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

    private static final Pattern ITEM_URL_SPLIT_PATTERN = Pattern.compile("[_.]");

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
        log.info("Logging out: {}", LOGOUT_PAGE);

        try {
            Response response = connectWithSessionCookies(LOGOUT_PAGE)
                    .method(Connection.Method.GET)
                    .execute();
        } catch (IOException e) {
            log.error("Logout at " + LOGOUT_PAGE + " failed", e);
            throw exceptionTranslator.translate(e);
        } finally {
            cookies.clear();
            // hat den Effekt, dass das Cookie "ilClientId" gelöscht wird
            // wenn wieder der Login aufgerufen wird, hat der Client noch das "authchallenge" Cookie
            // ist der Logout noch nötig?
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
        return document
                .select(COURSE_SELECTOR.getCssSelector())
                .stream()
                .map(this::toCourse)
                .collect(toList());
    }

    private Course toCourse(Element courseElement) {
        int courseId = getCourseId(courseElement);
        String courseName = courseElement.text();
        String courseUrl = courseElement.attr("href");
        return new Course(courseId, courseName, courseUrl);
    }

    private int getCourseId(@NonNull Element aTag) {
        String href = aTag.attr("href");
        // href="http://www.ilias.fh-dortmund.de/ilias/goto_ilias-fhdo_crs_\d+.html"
        String idString = href
                .replaceFirst(ILIAS_BASE_URL + "goto_ilias-fhdo_crs_", "")
                .replace(".html", "");
        // der Rest muss ein int sein
        return parseId(href, idString);
    }

    private int parseId(String href, String probableIdString) {
        try {
            return Integer.parseInt(probableIdString);
        } catch (NumberFormatException e) {
            throw new IliasItemIdStringParsingException(
                    format("Failed to parse '%s', original string was '%s'", probableIdString, href), e);
        }
    }

    @Override
    public Collection<Course> searchCoursesWithContent(@NonNull Collection<Course> selectedCourses) {
        return selectedCourses.stream()
                              .map(this::findCourseItems)
                              .collect(toList());
    }

    private Course findCourseItems(@NonNull Course course) {
        log.debug("Find all children for {}", course.getName());
        Collection<? extends CourseItem> childNodes = searchItemsRecursively(course.getUrl());
        return new Course(course.getId(), course.getName(), course.getUrl(), childNodes);
    }

    private Collection<? extends CourseItem> searchItemsRecursively(String itemUrl) {
        return connectAndGetDocument(itemUrl)
                .select(ITEM_CONTAINER_SELECTOR.getCssSelector())
                .stream()
                .map(this::toCourseItem)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toList());
    }

    private Optional<CourseItem> toCourseItem(Element itemContainer) {
        Elements itemTitle = itemContainer.select(ITEM_TITLE_SELECTOR.getCssSelector());
        String itemName = itemTitle.text();
        String itemUrl = itemTitle.attr("href");
        String idString = getItemIdFromUrl(itemUrl);
        String[] split = ITEM_URL_SPLIT_PATTERN.split(idString);

        String type = split[0];
        int itemId = Integer.parseInt(split[1]);

        List<String> properties = itemContainer.select(ITEM_PROPERTIES_SELECTOR.getCssSelector())
                                               .stream()
                                               .map(Element::text)
                                               .map(this::trimNbspFromString)
                                               .map(String::trim)
                                               .filter(s -> !s.isEmpty())
                                               .collect(toList());

        return createCourseItem(type, itemId, itemName, itemUrl, properties);
    }

    /**
     * Removes the "no backspace" ({@literal &nbsp;} character from the string.
     *
     * @param s
     *         String to trim
     * @return a new String without "no backspace" characters
     */
    private String trimNbspFromString(String s) {
        return s.replace("\u00a0", "");
    }

    private String getItemIdFromUrl(String itemUrl) {
        return itemUrl.replaceFirst(ILIAS_BASE_URL + "goto_ilias-fhdo_", "");
    }

    private Optional<CourseItem> createCourseItem(@NonNull String type, int itemId, @NonNull String itemName, @NonNull String itemUrl, List<String> properties) {
        switch (type) {
            case "fold":
                Collection<? extends CourseItem> courseItems = searchItemsRecursively(itemUrl);
                return Optional.of(new CourseFolder(itemId, itemName, itemUrl, courseItems));
            case "file":
                log.warn("itemId {}, name {}, url {}", itemId, itemName, itemUrl);
                String fileType = properties.get(0);
                return Optional.of(new CourseFile(itemId, itemName + "." + fileType, itemUrl));
            default:
                log.warn("Unknown type: {}, URL: {}", type, itemUrl);
        }
        return Optional.empty();
    }
}
