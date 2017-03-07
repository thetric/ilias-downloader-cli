package com.github.thetric.iliasdownloader.service.webparser.impl.course

import com.github.thetric.iliasdownloader.service.IliasService
import com.github.thetric.iliasdownloader.service.model.Course
import com.github.thetric.iliasdownloader.service.model.CourseFile
import com.github.thetric.iliasdownloader.service.model.CourseFolder
import com.github.thetric.iliasdownloader.service.model.IliasItem
import com.github.thetric.iliasdownloader.service.webparser.impl.IliasItemIdStringParsingException
import com.github.thetric.iliasdownloader.service.webparser.impl.course.datetime.RelativeDateTimeParser
import com.github.thetric.iliasdownloader.service.webparser.impl.course.jsoup.JSoupParserService
import com.github.thetric.iliasdownloader.service.webparser.impl.util.WebIoExceptionTranslator
import groovy.transform.CompileStatic
import groovy.util.logging.Log4j2
import org.apache.http.client.fluent.Executor
import org.apache.http.client.fluent.Request
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern

import static java.time.format.DateTimeFormatter.ofPattern

@CompileStatic
@Log4j2
final class CourseSyncServiceImpl implements CourseSyncService {
    private static final String COURSE_SELECTOR = "a[href*='_crs_'].il_ContainerItemTitle"
    private static final String ITEM_CONTAINER_SELECTOR = '.il_ContainerListItem'
    private static final String ITEM_TITLE_SELECTOR = 'a.il_ContainerItemTitle'
    private static final String ITEM_PROPERTIES_SELECTOR = '.il_ItemProperty'

    // these types should be ignored in logs
    private static final Set<String> IGNORED_ITEM_TYPES = new HashSet<>(['frm', 'grp'])

    private static final Pattern ITEM_URL_SPLIT_PATTERN = Pattern.compile("[_.]")

    // for German date time format: 23. Sep 2016, 17:34
    private static final DateTimeFormatter lastModifiedFormatter = ofPattern('dd. MMM yyyy, HH:mm', Locale.GERMAN)
    private final RelativeDateTimeParser relativeDateTimeParser

    private final WebIoExceptionTranslator exceptionTranslator

    private final JSoupParserService jSoupParserService

    private final String iliasBaseUrl
    private final String courseOverview
    private final String courseLinkPrefix

    CourseSyncServiceImpl(WebIoExceptionTranslator webIoExceptionTranslator,
                          JSoupParserService jSoupParserService,
                          String iliasBaseUrl, String clientId,
                          RelativeDateTimeParser relativeDateTimeParser) {
        this.exceptionTranslator = webIoExceptionTranslator
        this.jSoupParserService = jSoupParserService
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
        return document.select(COURSE_SELECTOR).collect { toCourse(it) }
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
    IliasService.VisitResult visit(
        final IliasItem courseItem,
        final Closure<IliasService.VisitResult> visitMethod, final Executor httpRequestExecutor) {

        for (IliasItem item : findItems(courseItem, httpRequestExecutor)) {
            def visitResult = visitMethod(item)
            if (visitResult == IliasService.VisitResult.TERMINATE) {
                return IliasService.VisitResult.TERMINATE
            }
            if (isNodeItem(item)) {
                def childResult = visit(item, visitMethod, httpRequestExecutor)
                if (childResult == IliasService.VisitResult.TERMINATE) {
                    return IliasService.VisitResult.TERMINATE
                }
            }
        }
        return IliasService.VisitResult.CONTINUE
    }

    private boolean isNodeItem(IliasItem iliasItem) {
        return iliasItem instanceof Course || iliasItem instanceof CourseFolder
    }

    private Collection<? extends IliasItem> findItems(final IliasItem courseItem, Executor httpRequestExecutor) {
        return getItemContainersFromUrl(courseItem.url, httpRequestExecutor)
            .collect({ toIliasItem(courseItem, it) })
            .findAll()
    }

    private Elements getItemContainersFromUrl(String itemUrl, Executor httpRequestExecutor) {
        // TODO replace with Kevin's Ilias Downloader 1 Method (directory output)
        // --> Performance?
        return connectAndGetDocument(itemUrl, httpRequestExecutor).select(ITEM_CONTAINER_SELECTOR)
    }

    private IliasItem toIliasItem(IliasItem parent, Element itemContainer) {
        Elements itemTitle = itemContainer.select(ITEM_TITLE_SELECTOR)
        String itemName = itemTitle.text()
        String itemUrl = itemTitle.attr('href')
        String idString = getItemIdFromUrl(itemUrl)
        String[] split = ITEM_URL_SPLIT_PATTERN.split(idString)

        String type = split[0]
        int itemId = Integer.parseInt(split[1])

        List<String> properties = getSanitizedItemProperties(itemContainer)

        return createIliasItem(parent, type, itemId, itemName, itemUrl, properties)
    }

    private List<String> getSanitizedItemProperties(Element itemContainer) {
        getItemProperties(itemContainer).collect { trimAllWhitespaces(it) }.findAll()
    }

    private String trimAllWhitespaces(Element element) {
        return trimNbspFromString(element.text()).trim()
    }

    private static Elements getItemProperties(Element itemContainer) {
        return itemContainer.select(ITEM_PROPERTIES_SELECTOR)
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

    private IliasItem createIliasItem(
        IliasItem parent,
        String type, int itemId,
        String itemName, String itemUrl, List<String> properties) {
        switch (type) {
            case 'fold':
                return new CourseFolder(itemId, itemName, itemUrl, parent)
            case 'file':
                log.debug('itemId {}, name {}, url {}', itemId, itemName, itemUrl)
                String fileType = properties.get(0)
                if (properties.size() < 3) {
                    throw new IllegalArgumentException("No last modified timestamp present! Item with " +
                        "ID $itemId (URL: $itemUrl) has only following properties: $properties")
                }
                LocalDateTime lastModified = parseDateString(properties.get(2))
                return new CourseFile(itemId, "$itemName.$fileType", itemUrl, parent, lastModified)
            default:
                if (!IGNORED_ITEM_TYPES.contains(type)) {
                    log.warn('Unknown type: {}, URL: {}', type, itemUrl)
                }
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
            def html = content.asString(StandardCharsets.UTF_8)
            return jSoupParserService.parse(html)
        } catch (IOException e) {
            log.error("Could not GET: $url", e)
            throw exceptionTranslator.translate(e)
        }
    }
}
