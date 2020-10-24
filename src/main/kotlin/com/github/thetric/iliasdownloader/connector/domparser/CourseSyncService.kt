package com.github.thetric.iliasdownloader.connector.domparser

import com.github.sardine.DavResource
import com.github.sardine.Sardine
import com.github.sardine.SardineFactory
import com.github.thetric.iliasdownloader.connector.api.IliasItemListener
import com.github.thetric.iliasdownloader.connector.api.model.Course
import com.github.thetric.iliasdownloader.connector.api.model.CourseFile
import com.github.thetric.iliasdownloader.connector.api.model.CourseFolder
import com.github.thetric.iliasdownloader.connector.api.model.LoginCredentials
import java.io.InputStream
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

internal class CourseSyncService(private val iliasHost: String) {
    private lateinit var sardine: Sardine

    fun login(loginCredentials: LoginCredentials) {
        sardine = SardineFactory.begin(loginCredentials.userName, loginCredentials.password)!!
    }

    fun <C> walkIliasCourse(course: Course, itemListener: IliasItemListener<C>, initialContext: C) {
        walkIliasDirectory(course.url, itemListener, initialContext)
    }

    private fun <C> walkIliasDirectory(url: String, itemListener: IliasItemListener<C>, context: C) {
        sardine.list(url)
            .drop(1) // drop self link
            .forEach {
                walkIliasItemNode(context, it, itemListener)
            }
    }

    private fun <C> walkIliasItemNode(
        parentContext: C,
        davResource: DavResource,
        itemListener: IliasItemListener<C>
    ) {
        val url = iliasHost + davResource.href.toString()
        if (davResource.isDirectory) {
            val courseFolder = CourseFolder(davResource.name, url)
            val newParentCtx = itemListener.handleFolder(parentContext, courseFolder)
            walkIliasDirectory(courseFolder.url, itemListener, newParentCtx)
        } else {
            val modifiedTimestamp = javaUtilDateToLocalDateTime(davResource.modified)
            val file = CourseFile(davResource.name, url, modifiedTimestamp, davResource.contentLength)
            itemListener.handleFile(parentContext, file)
        }
    }

    private fun javaUtilDateToLocalDateTime(date: Date): LocalDateTime {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
    }

    fun getContentAsStream(file: CourseFile): InputStream {
        return sardine.get(file.url)
    }
}
