package com.github.thetric.iliasdownloader.connector.domparser

import com.github.thetric.iliasdownloader.connector.api.ContextAwareIliasItemVisitor
import com.github.thetric.iliasdownloader.connector.api.IliasService
import com.github.thetric.iliasdownloader.connector.api.model.Course
import com.github.thetric.iliasdownloader.connector.api.model.CourseFile
import com.github.thetric.iliasdownloader.connector.api.model.LoginCredentials
import java.io.InputStream

/**
 * [IliasService] parsing the HTML from the Ilias website.
 */
internal class WebIliasService(
    private val courseSyncService: CourseSyncService,
    private val iliasWebClient: IliasWebClient
) : IliasService {

    override fun getContentAsStream(file: CourseFile): InputStream {
        return iliasWebClient.getAsInputStream(file.url)
    }

    override fun login(loginCredentials: LoginCredentials) {
        iliasWebClient.login(loginCredentials)
    }

    override fun logout() = iliasWebClient.logout()

    override fun getJoinedCourses() = courseSyncService.joinedCourses

    override fun <C> visit(
        courseItem: Course,
        itemVisitor: ContextAwareIliasItemVisitor<C>,
        initialContext: C
    ) {
        courseSyncService.visit(initialContext, courseItem, itemVisitor)
    }

    override fun close() {
        iliasWebClient.close()
    }
}
