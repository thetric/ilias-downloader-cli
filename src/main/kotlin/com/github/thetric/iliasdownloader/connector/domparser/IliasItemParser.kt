package com.github.thetric.iliasdownloader.connector.domparser

import com.github.thetric.iliasdownloader.connector.api.model.Course
import org.jsoup.nodes.Element

internal class IliasItemParser(
    private val courseWebDavPrefix: String,
    private val courseLinkPrefix: String
) {
    fun parseCourse(courseElement: Element): Course {
        val courseId = getCourseId(courseElement)
        val courseName = courseElement.text()
        val courseUrl = "$courseWebDavPrefix$courseId/"
        return Course(id = courseId, url = courseUrl, name = courseName)
    }

    private fun getCourseId(aTag: Element): Long {
        val href = aTag.attr("href")
        // href="http://www.ilias.fh-dortmund.de/ilias/goto_ilias-fhdo_crs_\d+.html"
        val idString = href.replaceFirst(courseLinkPrefix, "").replace(".html", "")
        // der Rest muss ein int sein
        return parseId(href, idString)
    }

}


private fun parseId(href: String, probableIdString: String): Long {
    try {
        return probableIdString.toLong()
    } catch (e: NumberFormatException) {
        val msg = "Failed to parse \'$probableIdString\', original string was \'$href\'"
        throw IliasItemIdStringParsingException(msg, e)
    }
}
