package com.github.thetric.iliasdownloader.service.webparser.impl.course

import com.github.thetric.iliasdownloader.service.IliasService
import com.github.thetric.iliasdownloader.service.exception.CourseItemNotFoundException
import com.github.thetric.iliasdownloader.service.model.Course
import com.github.thetric.iliasdownloader.service.model.CourseFile
import com.github.thetric.iliasdownloader.service.model.CourseFolder
import com.github.thetric.iliasdownloader.service.model.IliasItem
import com.github.thetric.iliasdownloader.service.webparser.impl.IliasItemIdStringParsingException
import com.github.thetric.iliasdownloader.service.webparser.impl.course.jsoup.JSoupParserService
import com.github.thetric.iliasdownloader.service.webparser.impl.util.WebIoExceptionTranslator
import groovy.transform.CompileStatic
import groovy.util.logging.Log4j2
import org.apache.http.client.fluent.Executor
import org.apache.http.client.fluent.Request
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.regex.Matcher

import static java.time.format.DateTimeFormatter.ofPattern

@CompileStatic
@Log4j2
final class CourseSyncServiceImpl implements CourseSyncService {
    private static final String COURSE_SELECTOR = "a[href*='_crs_'].il_ContainerItemTitle"

    // modified ISO 8601 formatter - the default from Java require a 'T' as date-time separator
    // e.g. 2016-11-15 14:13:59
    private static final DateTimeFormatter LAST_MODIFIED_FORMATTER = ofPattern('yyyy-MM-dd HH:mm:ss')
    private static final String COURSE_LINK_REGEX = /<a href="(?<url>.+)">(?<name>.+)<\/a>/
    private static final String ROW_SEPARATOR = '  '

    private final WebIoExceptionTranslator exceptionTranslator

    private final JSoupParserService jSoupParserService

    private final String iliasBaseUrl
    private final String courseOverview
    private final String courseLinkPrefix
    private final String courseWebDavPrefix

    CourseSyncServiceImpl(WebIoExceptionTranslator webIoExceptionTranslator,
                          JSoupParserService jSoupParserService,
                          String iliasBaseUrl, String clientId) {
        this.exceptionTranslator = webIoExceptionTranslator
        this.jSoupParserService = jSoupParserService

        this.iliasBaseUrl = iliasBaseUrl
        courseOverview = "${iliasBaseUrl}ilias.php?baseClass=ilPersonalDesktopGUI&cmd=jumpToSelectedItems"
        courseLinkPrefix = "${iliasBaseUrl}goto_${clientId}_crs_"
        courseWebDavPrefix = "${iliasBaseUrl}webdav.php/ilias-fhdo/ref_"
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
        String courseUrl = "${courseWebDavPrefix}$courseId/"
        return new Course(id: courseId, name: courseName, url: courseUrl)
    }

    private int getCourseId(Element aTag) {
        String href = aTag.attr('href')
        // href="http://www.ilias.fh-dortmund.de/ilias/goto_ilias-fhdo_crs_\d+.html"
        String idString = href.replaceFirst(courseLinkPrefix, '')
                              .replace('.html', '')
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
            IliasService.VisitResult visitResult = visitMethod(item)
            if (visitResult == IliasService.VisitResult.TERMINATE) {
                return IliasService.VisitResult.TERMINATE
            }
            if (isNodeItem(item)) {
                IliasService.VisitResult childResult = visit(item, visitMethod, httpRequestExecutor)
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

    private Collection<? extends IliasItem> findItems(final IliasItem courseItem, final Executor httpRequestExecutor) {
        String itemContainer = getItemContainersFromUrl(courseItem.url, httpRequestExecutor)
        if (!itemContainer.empty) {
            return getIliasItemRows(itemContainer, courseItem)*.trim()
                                                              .findAll()
                                                              .collect { toIliasItem(courseItem, it) }
                                                              .findAll()
        }
        throw new CourseItemNotFoundException('No items found at URL ', courseItem.url)
    }

    /**
     * Extract HTML from the 'table'
     * @param itemContainer
     * @param courseItem
     */
    private Collection<String> getIliasItemRows(final String tableHtml, final IliasItem courseItem) {
        String itemListStartDelimiter = '<hr>'
        int startIndexItemList = tableHtml.indexOf(itemListStartDelimiter)
        checkItemListIndex(startIndexItemList, 'Begin', courseItem)

        String itemListEndDelimiter = '\n<hr>'
        int endIndexItemList = tableHtml.lastIndexOf(itemListEndDelimiter)
        checkItemListIndex(endIndexItemList, 'End', courseItem)

        int itemListBeginPos = startIndexItemList + itemListStartDelimiter.length()
        if (itemListBeginPos >= endIndexItemList) {
            return []
        }
        return tableHtml[itemListBeginPos..endIndexItemList].split('\n')*.trim()
    }

    private void checkItemListIndex(final int index, final String name, final IliasItem item) {
        if (index == -1) {
            throw new IllegalArgumentException("$name of item list not found! Search URL is ${item.url}")
        }
    }

    private String getItemContainersFromUrl(final String itemUrl, final Executor httpRequestExecutor) {
        String html = this.getHtml(itemUrl, httpRequestExecutor)
        String startTag = '<pre>'
        int startIndexTable = html.indexOf(startTag)
        String endTag = '</pre>'
        int endIndexTable = html.lastIndexOf(endTag)
        int exclusiveStartIndex = startIndexTable + startTag.length()
        return html[exclusiveStartIndex..endIndexTable - 1]
    }

    private IliasItem toIliasItem(final IliasItem parent, final String itemRow) {
        // item format: (size or character '-' [=folder])  last modified  link
        if (isFolder(itemRow)) {
            return createFolder(parent, itemRow)
        }
        // assume it is a file
        return createFile(parent, itemRow)
    }

    private boolean isFolder(final String itemRow) {
        return itemRow[0] == '-'
    }

    private CourseFolder createFolder(final IliasItem parent, final String itemRow) {
        final int firstPosSeparator = itemRow.indexOf(ROW_SEPARATOR)
        final int secondPosSeparator = itemRow.indexOf(ROW_SEPARATOR, firstPosSeparator + ROW_SEPARATOR.length())
        Matcher parsedLink = parseLink(itemRow, secondPosSeparator)
        return new CourseFolder(
            name: parsedLink.group('name'),
            url: resolveItemLink(parent, parsedLink),
            parent: parent,
        )
    }

    private String resolveItemLink(final IliasItem parent, final Matcher parsedLink) {
        return "${parent.url}/${parsedLink.group('url')}"
    }

    private CourseFile createFile(final IliasItem parent, final String itemRow) {
        final int firstPosSeparator = itemRow.indexOf(ROW_SEPARATOR)
        final int secondPosSeparator = itemRow.indexOf(ROW_SEPARATOR, firstPosSeparator + ROW_SEPARATOR.length())

        Matcher parsedLinkName = parseLink(itemRow, secondPosSeparator)
        return new CourseFile(
            name: parsedLinkName.group('name'),
            url: resolveItemLink(parent, parsedLinkName),
            parent: parent,
            modified: parseLastModified(itemRow, firstPosSeparator, secondPosSeparator),
            size: parseFileSize(itemRow, firstPosSeparator)
        )
    }

    private long parseFileSize(final String itemRow, final int firstPosSeparator) {
        String rawSizeInBytes = itemRow[0..firstPosSeparator - 1]
        String sanitizedSizeInBytes = rawSizeInBytes.replaceAll(',', '')
        return Long.parseLong(sanitizedSizeInBytes)
    }

    private Matcher parseLink(final String itemRow, final int secondPosSeparator) {
        int startIndex = secondPosSeparator + ROW_SEPARATOR.length()
        Matcher matcher = itemRow[startIndex..-1] =~ COURSE_LINK_REGEX
        if (!matcher) {
            throw new IllegalStateException("Failed to parse $itemRow")
        }
        return matcher
    }

    private LocalDateTime parseLastModified(final String itemRow, final int firstPosSeparator, final int secondPosSep) {
        int startIndex = firstPosSeparator + ROW_SEPARATOR.length()
        String lastModifiedString = itemRow[startIndex..secondPosSep - 1]
        return LocalDateTime.parse(lastModifiedString, LAST_MODIFIED_FORMATTER)
    }

    private Document connectAndGetDocument(final String url, final Executor httpRequestExecutor) {
        final String html = getHtml(url, httpRequestExecutor)
        return jSoupParserService.parse(html)
    }

    private String getHtml(final String url, final Executor httpRequestExecutor) {
        log.debug('Getting: "{}"', url)
        try {
            return httpRequestExecutor.execute(Request.Get(url))
                                      .returnContent()
                                      .asString()
        } catch (IOException e) {
            log.error("Could not GET: $url", e)
            throw exceptionTranslator.translate(e)
        }
    }
}
