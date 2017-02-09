package com.github.thetric.iliasdownloader.service.webparser.impl

import com.github.thetric.iliasdownloader.service.IliasService
import com.github.thetric.iliasdownloader.service.exception.IliasAuthenticationException
import com.github.thetric.iliasdownloader.service.model.*
import groovy.transform.CompileStatic
import groovy.util.logging.Log4j2
import io.reactivex.Observable
import org.apache.http.client.fluent.Executor
import org.apache.http.client.fluent.Form
import org.apache.http.client.fluent.Request
import org.apache.http.impl.client.BasicCookieStore
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern

import static java.time.format.DateTimeFormatter.ofPattern

/**
 * @author broj
 * @since 31.05.2016
 */
@Log4j2
@CompileStatic
final class WebIliasService implements IliasService {
    private final WebIoExceptionTranslator exceptionTranslator
    private final FluentHcExecutorProvider fluentHcExecutorProvider

    private static final Pattern ITEM_URL_SPLIT_PATTERN = Pattern.compile("[_.]")

    // for German date time format: 23. Sep 2016, 17:34
    private static final DateTimeFormatter lastModifiedFormatter = ofPattern('dd. MMM yyyy, HH:mm', Locale.GERMAN)
    private final RelativeDateTimeParser relativeDateTimeParser = new GermanRelativeDateTimeParser()

    private final String iliasBaseUrl
    private final String loginPage
    private final String logoutPage
    private final String courseOverview
    private final String clientId
    private final String courseLinkPrefix

    // per default Groovy imports java.net.* where a CookieStore interface already exists
    // so we must use the full qualified import ;(
    private final org.apache.http.client.CookieStore cookieStore

    WebIliasService(WebIoExceptionTranslator exceptionTranslator, String iliasBaseUrl, String clientId, FluentHcExecutorProvider fluentHcExecutorProvider) {
        this.exceptionTranslator = exceptionTranslator
        this.iliasBaseUrl = iliasBaseUrl
        this.clientId = clientId
        loginPage = "${iliasBaseUrl}login.php"
        logoutPage = "${iliasBaseUrl}logout.php"
        courseOverview = "${iliasBaseUrl}ilias.php?baseClass=ilPersonalDesktopGUI&cmd=jumpToSelectedItems"
        courseLinkPrefix = "${iliasBaseUrl}goto_${clientId}_crs_"

        this.fluentHcExecutorProvider = fluentHcExecutorProvider
        cookieStore = new BasicCookieStore()
    }

    @Override
    InputStream getContentAsStream(CourseFile courseFile) {
        return connectWithSessionCookies().execute(Request.Get(courseFile.url))
                                          .returnContent()
                                          .asStream()
    }

    @Override
    void login(LoginCredentials loginCredentials) {
        log.info('Logging in at {}', loginPage)
        try {
            connectWithSessionCookies().execute(Request.Post(loginPage)
                                                       .bodyForm(Form.form()
                                                                     .add('username', loginCredentials.userName)
                                                                     .add('password', loginCredentials.password)
                                                                     .build()))
                                       .discardContent()
            if (!hasLoginCookie()) {
                cookieStore.clear()
                throw new IliasAuthenticationException('Ungültiger Login')
            }
        } catch (IOException e) {
            log.error("Login at $loginPage failed", e)
            cookieStore.clear()
            throw exceptionTranslator.translate(e)
        }
        log.info('Login at {} succeeded', loginPage)
    }

    private boolean hasLoginCookie() {
        return cookieStore.getCookies()
                          .any({ it.name == 'authchallenge' })
    }

    @Override
    void logout() {
        log.info('Logging out: {}', logoutPage)

        try {
            connectWithSessionCookies().execute(Request.Get(logoutPage))
                                       .discardContent()
        } catch (IOException e) {
            log.error("Logout at $logoutPage failed", e)
            throw exceptionTranslator.translate(e)
        } finally {
            cookieStore.clear()
        }
        log.info('Logout at {} succeeded', logoutPage)
    }

    private Executor connectWithSessionCookies() {
        return fluentHcExecutorProvider.createFluentHcExecutor(cookieStore)
    }

    private Document connectAndGetDocument(String url) {
        try {
            def content = connectWithSessionCookies().execute(Request.Get(url))
                                                     .returnContent()
            def html = content.asString()
            return Jsoup.parse(html)
        } catch (IOException e) {
            log.error("Could not GET: $url", e)
            throw exceptionTranslator.translate(e)
        }
    }

    @Override
    Collection<Course> getJoinedCourses() {
        log.info('Get all courses and groups from {}', courseOverview)
        Document document = connectAndGetDocument(courseOverview)
        return getCoursesFromHtml(document)
    }

    private Collection<Course> getCoursesFromHtml(Document document) {
        return Observable.fromIterable(document.select(CssSelectors.COURSE_SELECTOR.cssSelector))
                         .map({ toCourse(it) })
                         .blockingIterable()
                         .toList()
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
                if (properties.size() < 3) {
                    throw new IllegalArgumentException("No last modified timestamp present! Item with " +
                                                           "ID $itemId (URL: $itemUrl) has only following properties: $properties")
                }
                LocalDateTime lastModified = parseDateString(properties.get(2))
                return new CourseFile(itemId, "$itemName.$fileType", itemUrl, lastModified)
            default:
                log.warn("Unknown type: {}, URL: {}", type, itemUrl)
                return null
        }
    }

    private LocalDateTime parseDateString(String lastModifiedDateTimeString) {
        if (relativeDateTimeParser.isRelativeDateTime(lastModifiedDateTimeString)) {
            log.debug('{} is a relative date', lastModifiedDateTimeString)
            return relativeDateTimeParser.parse(lastModifiedDateTimeString)
        } else {
            return LocalDateTime.parse(lastModifiedDateTimeString, lastModifiedFormatter)
        }
    }
}
