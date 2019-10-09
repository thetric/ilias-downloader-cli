package com.github.thetric.iliasdownloader.connector.domparser.impl.course

import com.github.thetric.iliasdownloader.connector.api.model.Course
import com.github.thetric.iliasdownloader.connector.api.model.CourseFile
import com.github.thetric.iliasdownloader.connector.api.model.CourseFolder
import org.jsoup.nodes.Element

interface IliasItemParser {
    fun parseCourse(courseElement: Element): Course
    fun isFolder(itemRow: String): Boolean
    fun parseFolder(currentUrl: String, itemRow: String): CourseFolder
    fun parseFile(currentUrl: String, itemRow: String): CourseFile
}
