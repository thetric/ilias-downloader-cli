package com.github.thetric.iliasdownloader.service.webparser.impl.course

import com.github.thetric.iliasdownloader.service.IliasItemVisitor
import com.github.thetric.iliasdownloader.service.IliasItemVisitor.VisitResult
import com.github.thetric.iliasdownloader.service.exception.CourseItemNotFoundException
import com.github.thetric.iliasdownloader.service.model.Course
import com.github.thetric.iliasdownloader.service.model.CourseFile
import com.github.thetric.iliasdownloader.service.model.CourseFolder
import com.github.thetric.iliasdownloader.service.model.IliasItem
import com.github.thetric.iliasdownloader.service.webparser.impl.IliasItemIdStringParsingException
import com.github.thetric.iliasdownloader.service.webparser.impl.course.jsoup.JSoupParserService
import com.github.thetric.iliasdownloader.service.webparser.impl.webclient.IliasWebClient
import groovy.transform.CompileStatic
import groovy.transform.Immutable
import groovy.util.logging.Log4j2
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.regex.Matcher

import static com.github.thetric.iliasdownloader.service.IliasItemVisitor.VisitResult.CONTINUE
import static com.github.thetric.iliasdownloader.service.IliasItemVisitor.VisitResult.TERMINATE
import static java.time.format.DateTimeFormatter.ofPattern

@Immutable
final class ParsedIliasTableRow {
    final String name, url
}

/**
 * {@link CourseSyncService} based on HTML parsing.
 */
@CompileStatic
@Log4j2
final class CourseSyncServiceImpl implements CourseSyncService {
    private static final String COURSE_SELECTOR = "a[href*='_crs_'].il_ContainerItemTitle"

    // modified ISO 8601 formatter - the default from Java require a 'T' as date-time separator
    // e.g. 2016-11-15 14:13:59
    private static final DateTimeFormatter LAST_MODIFIED_FORMATTER = ofPattern('yyyy-MM-dd HH:mm:ss')
    private static final String COURSE_LINK_REGEX = /<a href="(?<url>.+)">(?<name>.+)<\/a>/
    private static final String ROW_SEPARATOR = '  '

    private final JSoupParserService jSoupParserService
    private final IliasWebClient webClient

    private final String iliasBaseUrl
    private final String courseOverview
    private final String courseLinkPrefix
    private final String courseWebDavPrefix

    CourseSyncServiceImpl(final JSoupParserService jSoupParserService,
                          final IliasWebClient iliasWebClient,
                          final String iliasBaseUrl, final String clientId) {
        this.jSoupParserService = jSoupParserService
        this.webClient = iliasWebClient

        this.iliasBaseUrl = iliasBaseUrl
        courseOverview = "${iliasBaseUrl}ilias.php?baseClass=ilPersonalDesktopGUI&cmd=jumpToSelectedItems"
        courseLinkPrefix = "${iliasBaseUrl}goto_${clientId}_crs_"
        courseWebDavPrefix = "${iliasBaseUrl}webdav.php/ilias-fhdo/ref_"
    }

    @Override
    Collection<Course> getJoinedCourses() {
        log.info('Get all courses and groups from {}', courseOverview)
        final Document document = connectAndGetDocument(courseOverview)
        return getCoursesFromHtml(document)
    }

    private Document connectAndGetDocument(final String url) {
        final String html = getHtml(url)
        return jSoupParserService.parse(html)
    }

    private String getHtml(final String url) {
        return webClient.getHtml(url)
    }

    private Collection<Course> getCoursesFromHtml(final Document document) {
        return document.select(COURSE_SELECTOR)
                       .collect { toCourse(it) }
    }

    private Course toCourse(final Element courseElement) {
        final int courseId = getCourseId(courseElement)
        final String courseName = courseElement.text()
        final String courseUrl = "$courseWebDavPrefix$courseId/"
        return new Course(id: courseId, name: courseName, url: courseUrl)
    }

    private int getCourseId(final Element aTag) {
        final String href = aTag.attr('href')
        // href="http://www.ilias.fh-dortmund.de/ilias/goto_ilias-fhdo_crs_\d+.html"
        final String idString = href.replaceFirst(courseLinkPrefix, '')
                                    .replace('.html', '')
        // der Rest muss ein int sein
        return parseId(href, idString)
    }

    private static int parseId(final String href, final String probableIdString) {
        try {
            return Integer.parseInt(probableIdString)
        } catch (final NumberFormatException e) {
            throw new IliasItemIdStringParsingException(
                "Failed to parse '$probableIdString', original string was '$href'", e)
        }
    }

    @Override
    VisitResult visit(final IliasItem courseItem, final IliasItemVisitor itemVisitor) {
        final String itemContainer = getItemContainersFromUrl(courseItem.url)
        if (!itemContainer.empty) {
            for (final String item in getNonEmptyEntries(itemContainer, courseItem)) {
                if (toIliasItem(courseItem, item, itemVisitor) == TERMINATE) {
                    return TERMINATE
                }
            }
            return CONTINUE
        }
        throw new CourseItemNotFoundException('No items found at URL ', courseItem.url)
    }

    private List<String> getNonEmptyEntries(final String itemContainer, final IliasItem courseItem) {
        return getIliasItemRows(itemContainer, courseItem)*.trim()
                                                          .findAll()
    }

    /**
     * Extract HTML from the 'table'
     * @param itemContainer
     * @param courseItem
     */
    private Collection<String> getIliasItemRows(final String tableHtml, final IliasItem courseItem) {
        final String itemListStartDelimiter = '<hr>'
        final int startIndexItemList = tableHtml.indexOf(itemListStartDelimiter)
        checkItemListIndex(startIndexItemList, 'Begin', courseItem)

        final String itemListEndDelimiter = '\n<hr>'
        final int endIndexItemList = tableHtml.lastIndexOf(itemListEndDelimiter)
        checkItemListIndex(endIndexItemList, 'End', courseItem)

        final int itemListBeginPos = startIndexItemList + itemListStartDelimiter.length()
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

    private String getItemContainersFromUrl(final String itemUrl) {
        final String html = this.getHtml(itemUrl)
        final String startTag = '<pre>'
        final int startIndexTable = html.indexOf(startTag)
        final String endTag = '</pre>'
        final int endIndexTable = html.lastIndexOf(endTag)
        final int exclusiveStartIndex = startIndexTable + startTag.length()
        return html[exclusiveStartIndex..endIndexTable - 1]
    }

    private VisitResult toIliasItem(final IliasItem parent, final String itemRow, final IliasItemVisitor itemVisitor) {
        if (isFolder(itemRow)) {
            final CourseFolder courseFolder = createFolder(parent, itemRow)

            final VisitResult folderVisitResult = itemVisitor.handleFolder(courseFolder)
            if (folderVisitResult == TERMINATE) {
                return TERMINATE
            }

            final VisitResult childrenVisitResult = visit(courseFolder, itemVisitor)
            return childrenVisitResult
        }
        // assume it is a file
        return itemVisitor.handleFile(createFile(parent, itemRow))
    }

    private boolean isFolder(final String itemRow) {
        return itemRow[0] == '-'
    }

    private CourseFolder createFolder(final IliasItem parent, final String itemRow) {
        final int firstPosSeparator = itemRow.indexOf(ROW_SEPARATOR)
        final int secondPosSeparator = itemRow.indexOf(ROW_SEPARATOR, firstPosSeparator + ROW_SEPARATOR.length())
        final ParsedIliasTableRow parsedLink = parseLink(itemRow, secondPosSeparator)
        return new CourseFolder(name: parsedLink.name, url: resolveItemLink(parent, parsedLink.url), parent: parent)
    }

    private String resolveItemLink(final IliasItem parent, final String relUrl) {
        return "$parent.url/$relUrl"
    }

    private CourseFile createFile(final IliasItem parent, final String itemRow) {
        final int firstPosSeparator = itemRow.indexOf(ROW_SEPARATOR)
        final int secondPosSeparator = itemRow.indexOf(ROW_SEPARATOR, firstPosSeparator + ROW_SEPARATOR.length())

        final ParsedIliasTableRow parsedLinkName = parseLink(itemRow, secondPosSeparator)
        return new CourseFile(
            name: parsedLinkName.name,
            url: resolveItemLink(parent, parsedLinkName.url),
            parent: parent,
            modified: parseLastModified(itemRow, firstPosSeparator, secondPosSeparator),
            size: parseFileSize(itemRow, firstPosSeparator)
        )
    }

    private long parseFileSize(final String itemRow, final int firstPosSeparator) {
        final String rawSizeInBytes = itemRow[0..firstPosSeparator - 1]
        final String sanitizedSizeInBytes = rawSizeInBytes.replaceAll(',', '')
        return Long.parseLong(sanitizedSizeInBytes)
    }

    private ParsedIliasTableRow parseLink(final String itemRow, final int secondPosSeparator) {
        final int startIndex = secondPosSeparator + ROW_SEPARATOR.length()
        final Matcher matcher = itemRow[startIndex..-1] =~ COURSE_LINK_REGEX
        if (!matcher) {
            throw new IllegalStateException("Failed to parse $itemRow")
        }
        return new ParsedIliasTableRow(matcher.group('name'), matcher.group('url'))
    }

    private LocalDateTime parseLastModified(final String itemRow, final int firstPosSeparator, final int secondPosSep) {
        final int startIndex = firstPosSeparator + ROW_SEPARATOR.length()
        final String lastModifiedString = itemRow[startIndex..secondPosSep - 1]
        return LocalDateTime.parse(lastModifiedString, LAST_MODIFIED_FORMATTER)
    }
}
