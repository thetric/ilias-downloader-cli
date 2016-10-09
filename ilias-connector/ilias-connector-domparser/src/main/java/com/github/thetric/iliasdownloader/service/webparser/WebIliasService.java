package com.github.thetric.iliasdownloader.service.webparser;

import com.github.thetric.iliasdownloader.service.IliasService;
import com.github.thetric.iliasdownloader.service.exception.IliasAuthenticationException;
import com.github.thetric.iliasdownloader.service.model.*;
import io.reactivex.Observable;
import io.reactivex.Single;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

import static java.lang.String.format;

/**
 * @author broj
 * @since 31.05.2016
 */
@Log4j2
final class WebIliasService implements IliasService {
    @NonNull
    private final WebIoExceptionTranslator exceptionTranslator;
    private static final Pattern ITEM_URL_SPLIT_PATTERN = Pattern.compile("[_.]");

    private final String iliasBaseUrl;
    private final String loginPage;
    private final String logoutPage;
    private final String courseOverview;
    private final String clientId;
    private final String courseLinkPrefix;

    @NonNull
    private Map<String, String> cookies;

    WebIliasService(@NonNull WebIoExceptionTranslator exceptionTranslator, @NonNull String iliasBaseUrl, @NonNull String clientId) {
        this.exceptionTranslator = exceptionTranslator;
        this.iliasBaseUrl = iliasBaseUrl;
        this.clientId = clientId;
        loginPage = iliasBaseUrl + "login.php";
        logoutPage = iliasBaseUrl + "logout.php";
        courseOverview = iliasBaseUrl + "ilias.php?baseClass=ilPersonalDesktopGUI&cmd=jumpToSelectedItems";
        courseLinkPrefix = iliasBaseUrl + "goto_" + clientId + "_crs_";
        cookies = new HashMap<>();
    }

    public Single<byte[]> getContent(CourseFile courseFile) {
    }

    @Override
    public void login(@NonNull LoginCredentials loginCredentials) {
        Connection.Response response;
        log.info("Logging in at {}", loginPage);
        try {
            response = Jsoup
                    .connect(loginPage)
                    .data("username", loginCredentials.getUserName())
                    .data("password", loginCredentials.getPassword())
                    .method(Connection.Method.POST)
                    .execute();
        } catch (IOException e) {
            log.error("Login at " + loginPage + " failed", e);
            throw exceptionTranslator.translate(e);
        }
        log.info("Login at {} succeeded", loginPage);

        ensureAuthentication(response);
        cookies = response.cookies();
    }

    private void ensureAuthentication(@NonNull Connection.Response response) {
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
        log.info("Logging out: {}", logoutPage);

        try {
            Connection.Response response = connectWithSessionCookies(logoutPage)
                    .method(Connection.Method.GET)
                    .execute();
        } catch (IOException e) {
            log.error("Logout at " + logoutPage + " failed", e);
            throw exceptionTranslator.translate(e);
        } finally {
            cookies.clear();
            // hat den Effekt, dass das Cookie "ilClientId" gelöscht wird
            // wenn wieder der Login aufgerufen wird, hat der Client noch das "authchallenge" Cookie
            // ist der Logout noch nötig?
        }
        log.info("Logout at {} succeeded", logoutPage);
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
            Connection.Response response = connectWithSessionCookies(url).execute();
            return response.parse();
        } catch (IOException e) {
            log.error("Could not GET: " + url, e);
            throw exceptionTranslator.translate(e);
        }
    }

    @Override
    public io.reactivex.Observable<Course> getJoinedCourses() {
        log.info("Get all courses and groups from {}", courseOverview);
        Document document = connectAndGetDocument(courseOverview);
        return getCoursesFromHtml(document);
    }

    private Observable<Course> getCoursesFromHtml(@NonNull Document document) {
        return Observable
                .fromIterable(document.select(CssSelectors.COURSE_SELECTOR.getCssSelector()))
                .map(this::toCourse);
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
                .replaceFirst(courseLinkPrefix, "")
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
    public Observable<Course> searchCoursesWithContent(@NonNull Collection<Course> selectedCourses) {
        return Observable.fromIterable(selectedCourses)
                         .map(this::findCourseItems);
    }

    private Course findCourseItems(@NonNull Course course) {
        log.debug("Find all children for {}", course.getName());
        Collection<? extends CourseItem> childNodes = searchItemsRecursively(course.getUrl());
        return new Course(course.getId(), course.getName(), course.getUrl(), childNodes);
    }

    private Collection<? extends CourseItem> searchItemsRecursively(String itemUrl) {
        return Observable.fromIterable(connectAndGetDocument(itemUrl)
                                               .select(CssSelectors.ITEM_CONTAINER_SELECTOR.getCssSelector()))
                         .map(this::toCourseItem)
                         .filter(Optional::isPresent)
                         .map(Optional::get)
                         .toList()
                         .blockingGet();
    }

    private Optional<CourseItem> toCourseItem(Element itemContainer) {
        Elements itemTitle = itemContainer.select(CssSelectors.ITEM_TITLE_SELECTOR.getCssSelector());
        String itemName = itemTitle.text();
        String itemUrl = itemTitle.attr("href");
        String idString = getItemIdFromUrl(itemUrl);
        String[] split = ITEM_URL_SPLIT_PATTERN.split(idString);

        String type = split[0];
        int itemId = Integer.parseInt(split[1]);

        List<String> properties = Observable.fromIterable(itemContainer.select(CssSelectors.ITEM_PROPERTIES_SELECTOR.getCssSelector()))
                                            .map(Element::text)
                                            .map(this::trimNbspFromString)
                                            .map(String::trim)
                                            .filter(s -> !s.isEmpty())
                                            .toList()
                                            .blockingGet();

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
        return itemUrl.replaceFirst(iliasBaseUrl + "goto_ilias-fhdo_", "");
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
