package com.github.thetric.iliasdownloader.service.webparser.impl.course

import com.github.thetric.iliasdownloader.service.model.Course
import com.github.thetric.iliasdownloader.service.model.CourseFile
import com.github.thetric.iliasdownloader.service.model.CourseFolder
import com.github.thetric.iliasdownloader.service.model.CourseItem
import com.github.thetric.iliasdownloader.service.webparser.impl.IliasItemIdStringParsingException
import com.github.thetric.iliasdownloader.service.webparser.impl.util.WebIoExceptionTranslator
import com.github.thetric.iliasdownloader.service.webparser.impl.util.datetime.RelativeDateTimeParser
import groovy.transform.CompileStatic
import groovy.util.logging.Log4j2
import io.reactivex.Observable
import org.apache.http.client.fluent.Executor
import org.apache.http.client.fluent.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern

import static java.time.format.DateTimeFormatter.ofPattern

@CompileStatic
@Log4j2
final class CourseSyncServiceImpl implements CourseSyncService {
    private final WebIoExceptionTranslator exceptionTranslator

    private static final Pattern ITEM_URL_SPLIT_PATTERN = Pattern.compile("[_.]")

    // for German date time format: 23. Sep 2016, 17:34
    private static final DateTimeFormatter lastModifiedFormatter = ofPattern('dd. MMM yyyy, HH:mm', Locale.GERMAN)
    private final RelativeDateTimeParser relativeDateTimeParser

    private final String iliasBaseUrl
    private final String courseOverview
    private final String courseLinkPrefix


    CourseSyncServiceImpl(WebIoExceptionTranslator webIoExceptionTranslator,
                          String iliasBaseUrl, String clientId,
                          RelativeDateTimeParser relativeDateTimeParser) {
        this.exceptionTranslator = webIoExceptionTranslator
        this.relativeDateTimeParser = relativeDateTimeParser

        this.iliasBaseUrl = iliasBaseUrl
        courseOverview = "${iliasBaseUrl}ilias.php?baseClass=ilPersonalDesktopGUI&cmd=jumpToSelectedItems"
        courseLinkPrefix = "${iliasBaseUrl}goto_${clientId}_crs_"
    }

    @Override
    Collection<Course> getJoinedCourses(Executor httpRequestExecutor) {
        log.info('Get all courses and groups from {}', courseOverview)
        Document document = connectAndGetDocument(courseOverview, httpRequestExecutor)
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
    Observable<Course> searchCoursesWithContent(Collection<Course> selectedCourses, Executor httpRequestExecutor) {
        return Observable.fromIterable(selectedCourses)
                         .map({ findCourseItems(it, httpRequestExecutor) })
    }

    private Course findCourseItems(Course course, Executor httpRequestExecutor) {
        log.debug('Find all children for {}', course.name)
        Collection<? extends CourseItem> childNodes = searchItemsRecursively(course.url, httpRequestExecutor)
        return new Course(course.id, course.name, course.url, childNodes)
    }

    private Collection<? extends CourseItem> searchItemsRecursively(String itemUrl, Executor httpRequestExecutor) {
        def list = new ArrayList<>()
        for (Element element : getItemContainersFromUrl(itemUrl, httpRequestExecutor)) {
            def item = toCourseItem(element, httpRequestExecutor)
            if (item) list << item
        }
        return list
    }

    private Elements getItemContainersFromUrl(String itemUrl, Executor httpRequestExecutor) {
        return connectAndGetDocument(itemUrl, httpRequestExecutor).
            select(CssSelectors.ITEM_CONTAINER_SELECTOR.getCssSelector())
    }

    private CourseItem toCourseItem(Element itemContainer, Executor httpRequestExecutor) {
        Elements itemTitle = itemContainer.select(CssSelectors.ITEM_TITLE_SELECTOR.getCssSelector())
        String itemName = itemTitle.text()
        String itemUrl = itemTitle.attr('href')
        String idString = getItemIdFromUrl(itemUrl)
        String[] split = ITEM_URL_SPLIT_PATTERN.split(idString)

        String type = split[0]
        int itemId = Integer.parseInt(split[1])

        List<String> properties = getSanitizedItemProperties(itemContainer)

        return createCourseItem(type, itemId, itemName, itemUrl, properties, httpRequestExecutor)
    }

    private List<String> getSanitizedItemProperties(Element itemContainer) {
        def itemProps = new ArrayList<>()
        for (Element element : getItemProperties(itemContainer)) {
            String text = trimAllWhitespaces(element)
            if (text && !text.empty) {
                itemProps << text
            }
        }
        return itemProps
    }

    private String trimAllWhitespaces(Element element) {
        return trimNbspFromString(element.text()).trim()
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
        String itemName, String itemUrl, List<String> properties,
        Executor httpRequestExecutor) {
        switch (type) {
            case 'fold':
                Collection<? extends CourseItem> courseItems = searchItemsRecursively(itemUrl, httpRequestExecutor)
                return new CourseFolder(itemId, itemName, itemUrl, courseItems)
            case 'file':
                log.debug("itemId {}, name {}, url {}", itemId, itemName, itemUrl)
                String fileType = properties.get(0)
                if (properties.size() < 3) {
                    throw new IllegalArgumentException("No last modified timestamp present! Item with " +
                                                           "ID $itemId (URL: $itemUrl) has only following properties: $properties")
                }
                LocalDateTime lastModified = parseDateString(properties.get(2))
                return new CourseFile(itemId, "$itemName.$fileType", itemUrl, lastModified)
            default:
                log.warn('Unknown type: {}, URL: {}', type, itemUrl)
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

    private Document connectAndGetDocument(String url, Executor httpRequestExecutor) {
        log.debug('Getting: "{}"', url)
        try {
            def content = httpRequestExecutor.execute(Request.Get(url))
                                             .returnContent()
            String html = content.asString()
            return Jsoup.parse(html)
        } catch (IOException e) {
            log.error("Could not GET: $url", e)
            throw exceptionTranslator.translate(e)
        }
    }
}
