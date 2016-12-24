package com.github.thetric.iliasdownloader.service.webparser

import com.github.thetric.iliasdownloader.service.IliasService
import com.github.thetric.iliasdownloader.service.exception.CourseItemNotFoundException
import com.github.thetric.iliasdownloader.service.exception.IliasAuthenticationException
import com.github.thetric.iliasdownloader.service.model.*
import groovy.transform.CompileStatic
import groovy.util.logging.Log4j2
import io.reactivex.Observable
import io.reactivex.Single
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

import java.util.regex.Pattern

/**
 * @author broj
 * @since 31.05.2016
 */
@Log4j2
@CompileStatic
final class WebIliasService implements IliasService {
    private static final int HTTP_FOUND = 302
    private final WebIoExceptionTranslator exceptionTranslator
    private static final int CONNECTION_TIMEOUT = 7_000
    private static final Pattern ITEM_URL_SPLIT_PATTERN = Pattern.compile("[_.]")

    private final String iliasBaseUrl
    private final String loginPage
    private final String logoutPage
    private final String courseOverview
    private final String clientId
    private final String courseLinkPrefix

    private Map<String, String> cookies

    WebIliasService(WebIoExceptionTranslator exceptionTranslator, String iliasBaseUrl, String clientId) {
        this.exceptionTranslator = exceptionTranslator
        this.iliasBaseUrl = iliasBaseUrl
        this.clientId = clientId
        loginPage = "${iliasBaseUrl}login.php"
        logoutPage = "${iliasBaseUrl}logout.php"
        courseOverview = "${iliasBaseUrl}ilias.php?baseClass=ilPersonalDesktopGUI&cmd=jumpToSelectedItems"
        courseLinkPrefix = "${iliasBaseUrl}goto_${clientId}_crs_"
        cookies = new HashMap<>()
    }

    Single<byte[]> getContent(CourseFile courseFile) {
        return Single.create {
            try {
                Connection.Response response = connectWithSessionCookies(courseFile.getUrl())
                        .ignoreContentType(true)
                        .followRedirects(false)
                        .execute()
                checkResponseStatus(response)
                it.onSuccess(response.bodyAsBytes())
            } catch (Exception ex) {
                it.onError(ex)
            }
        }
    }

    private static void checkResponseStatus(Connection.Response response) {
        log.debug('response.statusCode() = {}', response.statusCode())
        log.debug('response.statusMessage() = {}', response.statusMessage())
        switch (response.statusCode()) {
            case HTTP_FOUND:
                // might the session be expired?
                throw new CourseItemNotFoundException('Course item not found, got a redirect',
                        response.url().toExternalForm())
        }
    }

    @Override
    void login(LoginCredentials loginCredentials) {
        Connection.Response response
        log.info('Logging in at {}', loginPage)
        try {
            response = Jsoup.connect(loginPage)
                            .data('username', loginCredentials.getUserName())
                            .data('password', loginCredentials.getPassword())
                            .method(Connection.Method.POST)
                            .execute()
        } catch (IOException e) {
            log.error("Login at $loginPage failed", e)
            throw exceptionTranslator.translate(e)
        }
        ensureAuthentication(response)
        cookies = response.cookies()

        log.info('Login at {} succeeded', loginPage)
    }

    private static void ensureAuthentication(Connection.Response response) {
        // diese Prüfung ist nicht 100% wasserdicht. Das Cookie kann trotzdem gesetzt worden sein, wenn sich der Nutzer
        // zuvor erfolgreich ein- und wieder ausgeloggt hat und sich dann der Login fehlschlägt
        // es wäre vielleicht sicherer, die URL zu prüfen
        boolean isAuthenticated = response.cookies().containsKey('authchallenge')
        if (!isAuthenticated) {
            // bessere Fehlermeldung?
            throw new IliasAuthenticationException('Login fehlgeschlagen')
        }
    }

    @Override
    void logout() {
        log.info('Logging out: {}', logoutPage)

        try {
            connectWithSessionCookies(logoutPage)
                    .method(Connection.Method.GET)
                    .execute()
        } catch (IOException e) {
            log.error("Logout at $logoutPage failed", e)
            throw exceptionTranslator.translate(e)
        } finally {
            cookies.clear()
            // hat den Effekt, dass das Cookie "ilClientId" gelöscht wird
            // wenn wieder der Login aufgerufen wird, hat der Client noch das "authchallenge" Cookie
            // ist der Logout noch nötig?
        }
        log.info('Logout at {} succeeded', logoutPage)
    }

    private Connection connectWithSessionCookies(String iliasWebsite) {
        if (cookies.isEmpty()) {
            throw new NoCookiesAvailableException('No cookies found. Ensure you are already logged in')
        }
        return Jsoup.connect(iliasWebsite)
                    .cookies(cookies)
                    .timeout(CONNECTION_TIMEOUT)
    }

    private Document connectAndGetDocument(String url) {
        try {
            Connection.Response response = connectWithSessionCookies(url).execute()
            return response.parse()
        } catch (IOException e) {
            log.error("Could not GET: $url", e)
            throw exceptionTranslator.translate(e)
        }
    }

    @Override
    Observable<Course> getJoinedCourses() {
        log.info('Get all courses and groups from {}', courseOverview)
        Document document = connectAndGetDocument(courseOverview)
        return getCoursesFromHtml(document)
    }

    private Observable<Course> getCoursesFromHtml(Document document) {
        return Observable.fromIterable(document.select(CssSelectors.COURSE_SELECTOR.cssSelector))
                         .map({ toCourse(it) })
    }

    private Course toCourse(Element courseElement) {
        int courseId = getCourseId(courseElement)
        String courseName = courseElement.text()
        String courseUrl = courseElement.attr('href')
        return new Course(courseId, courseName, courseUrl)
    }

    private int getCourseId(Element aTag) {
        String href = aTag.attr('href')
        // href="http://www.ilias.fh-dortmund.de/ilias/goto_ilias-fhdo_crs_\d+.html"
        String idString = href.replaceFirst(courseLinkPrefix, "")
                              .replace(".html", "")
        // der Rest muss ein int sein
        return parseId(href, idString)
    }

    private static int parseId(String href, String probableIdString) {
        try {
            return Integer.parseInt(probableIdString)
        } catch (NumberFormatException e) {
            throw new IliasItemIdStringParsingException(
                    "Failed to parse '$probableIdString', original string was '$href'", e)
        }
    }

    @Override
    Observable<Course> searchCoursesWithContent(Collection<Course> selectedCourses) {
        return Observable.fromIterable(selectedCourses)
                         .map({ findCourseItems(it) })
    }

    private Course findCourseItems(Course course) {
        log.debug('Find all children for {}', course.name)
        Collection<? extends CourseItem> childNodes = searchItemsRecursively(course.url)
        return new Course(course.id, course.name, course.url, childNodes)
    }

    private Collection<? extends CourseItem> searchItemsRecursively(String itemUrl) {
        return Observable.fromIterable(getItemContainersFromUrl(itemUrl))
                         .map({ toCourseItem(it) })
                         .filter({ it.present })
                         .map({ it.get() })
                         .toList()
                         .blockingGet()
    }

    private Elements getItemContainersFromUrl(String itemUrl) {
        return connectAndGetDocument(itemUrl).select(CssSelectors.ITEM_CONTAINER_SELECTOR.getCssSelector())
    }

    private Optional<CourseItem> toCourseItem(Element itemContainer) {
        Elements itemTitle = itemContainer.select(CssSelectors.ITEM_TITLE_SELECTOR.getCssSelector())
        String itemName = itemTitle.text()
        String itemUrl = itemTitle.attr("href")
        String idString = getItemIdFromUrl(itemUrl)
        String[] split = ITEM_URL_SPLIT_PATTERN.split(idString)

        String type = split[0]
        int itemId = Integer.parseInt(split[1])

        List<String> properties = Observable.fromIterable(getItemProperties(itemContainer))
                                            .map({ it.text() })
                                            .map({ trimNbspFromString(it) })
                                            .map({ it.trim() })
                                            .filter({ !it.empty })
                                            .toList()
                                            .blockingGet()

        return Optional.ofNullable(createCourseItem(type, itemId, itemName, itemUrl, properties))
    }

    private static Elements getItemProperties(Element itemContainer) {
        return itemContainer.select(CssSelectors.ITEM_PROPERTIES_SELECTOR.getCssSelector())
    }

    /**
     * Removes the "no backspace" ({@literal &nbsp} character from the string.
     *
     * @param s
     *         String to trim
     * @return a new String without "no backspace" characters
     */
    private static String trimNbspFromString(String s) {
        return s.replace('\u00a0', '')
    }

    private String getItemIdFromUrl(String itemUrl) {
        return itemUrl.replaceFirst("${iliasBaseUrl}goto_ilias-fhdo_", '')
    }

    private CourseItem createCourseItem(
            String type, int itemId,
            String itemName, String itemUrl, List<String> properties) {
        switch (type) {
            case "fold":
                Collection<? extends CourseItem> courseItems = searchItemsRecursively(itemUrl)
                return new CourseFolder(itemId, itemName, itemUrl, courseItems)
            case "file":
                log.debug("itemId {}, name {}, url {}", itemId, itemName, itemUrl)
                String fileType = properties.get(0)
                return new CourseFile(itemId, "$itemName.$fileType", itemUrl, null)
            default:
                log.warn("Unknown type: {}, URL: {}", type, itemUrl)
                return null
        }
    }
}
