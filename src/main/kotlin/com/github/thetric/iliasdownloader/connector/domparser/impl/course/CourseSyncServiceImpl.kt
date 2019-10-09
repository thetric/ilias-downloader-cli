package com.github.thetric.iliasdownloader.connector.domparser.impl.course

import com.github.thetric.iliasdownloader.connector.api.ContextAwareIliasItemVisitor
import com.github.thetric.iliasdownloader.connector.api.model.Course
import com.github.thetric.iliasdownloader.connector.api.model.IliasItem
import com.github.thetric.iliasdownloader.connector.domparser.impl.course.jsoup.JSoupParserService
import com.github.thetric.iliasdownloader.connector.domparser.impl.webclient.IliasWebClient
import mu.KotlinLogging
import org.jsoup.nodes.Document

private const val COURSE_SELECTOR = "a[href*='_crs_'].il_ContainerItemTitle"


/**
 * [CourseSyncService] based on HTML parsing.
 */
class CourseSyncServiceImpl(
    private val jSoupParserService: JSoupParserService,
    private val webClient: IliasWebClient,
    private val itemParser: IliasItemParser,
    private val courseOverview: String
) : CourseSyncService {
    private val log = KotlinLogging.logger {}

    override val joinedCourses: Collection<Course>
        get() {
            log.info { "Get all courses and groups from $courseOverview" }
            val document = connectAndGetDocument(courseOverview)
            return getCoursesFromHtml(document)
        }

    private fun connectAndGetDocument(url: String): Document {
        val html = getHtml(url)
        return jSoupParserService.parse(html)
    }

    private fun getHtml(url: String): String {
        return webClient.getHtml(url)
    }

    private fun getCoursesFromHtml(document: Document): Collection<Course> {
        return document.select(COURSE_SELECTOR)
            .map { itemParser.parseCourse(it) }
    }

    override fun <C> visit(
        parentContext: C,
        courseItem: IliasItem,
        itemVisitor: ContextAwareIliasItemVisitor<C>
    ) {
        val itemContainer = getItemContainersFromUrl(courseItem.url)
        for (entry in getNonEmptyEntries(itemContainer, courseItem)) {
            walkIliasItemNode(
                parentContext,
                courseItem.url,
                entry,
                itemVisitor
            )
        }
    }

    private fun getItemContainersFromUrl(itemUrl: String): String {
        val html = getHtml(itemUrl)
        val startTag = "<pre>"
        val startIndexTable = html.indexOf(startTag)
        val endTag = "</pre>"
        val endIndexTable = html.lastIndexOf(endTag)
        val exclusiveStartIndex = startIndexTable + startTag.length
        return html.substring(exclusiveStartIndex, endIndexTable - 1)
    }

    private fun getNonEmptyEntries(
        itemContainer: String,
        courseItem: IliasItem
    ): List<String> {
        return getIliasItemRows(itemContainer, courseItem).filter {
            !it.isBlank()
        }
    }

    private fun getIliasItemRows(
        tableHtml: String,
        courseItem: IliasItem
    ): Collection<String> {
        val itemListStartDelimiter = "<hr>"
        val startIndexItemList = tableHtml.indexOf(itemListStartDelimiter)
        checkItemListIndex(startIndexItemList, "Begin", courseItem)

        val itemListEndDelimiter = "\n<hr>"
        val endIndexItemList = tableHtml.lastIndexOf(itemListEndDelimiter)
        checkItemListIndex(endIndexItemList, "End", courseItem)

        val itemListBeginPos =
            startIndexItemList + itemListStartDelimiter.length
        return if (itemListBeginPos >= endIndexItemList) {
            listOf()
        } else tableHtml.subSequence(itemListBeginPos, endIndexItemList)
            .split("\n")
            .map { it.trim() }
    }

    private fun checkItemListIndex(index: Int, name: String, item: IliasItem) {
        if (index == -1) {
            throw IllegalArgumentException("$name of item list not found! Search URL is ${item.url}")
        }
    }

    private fun <C> walkIliasItemNode(
        parentContext: C,
        currentUrl: String,
        itemRow: String,
        itemVisitor: ContextAwareIliasItemVisitor<C>
    ) {
        if (itemParser.isFolder(itemRow)) {
            val courseFolder = itemParser.parseFolder(currentUrl, itemRow)
            val newParentCtx =
                itemVisitor.handleFolder(parentContext, courseFolder)
            visit(newParentCtx, courseFolder, itemVisitor)
        } else {
            // assume it is a file
            itemVisitor.handleFile(
                parentContext,
                itemParser.parseFile(currentUrl, itemRow)
            )
        }
    }

}
