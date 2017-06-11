package com.github.thetric.iliasdownloader.service.webparser.impl

import com.github.thetric.iliasdownloader.service.IliasItemVisitor
import com.github.thetric.iliasdownloader.service.IliasService
import com.github.thetric.iliasdownloader.service.model.Course
import com.github.thetric.iliasdownloader.service.model.CourseFile
import com.github.thetric.iliasdownloader.service.model.LoginCredentials
import com.github.thetric.iliasdownloader.service.webparser.impl.course.CourseSyncService
import com.github.thetric.iliasdownloader.service.webparser.impl.webclient.IliasWebClient
import groovy.transform.CompileStatic
import groovy.util.logging.Log4j2

/**
 * {@link IliasService} parsing the HTML from the Ilias website.
 */
@Log4j2
@CompileStatic
final class WebIliasService implements IliasService {
    private final CourseSyncService courseSyncService
    private final IliasWebClient iliasWebClient

    WebIliasService(final CourseSyncService courseSyncService, final IliasWebClient iliasWebClient) {
        this.courseSyncService = courseSyncService
        this.iliasWebClient = iliasWebClient
    }

    @Override
    InputStream getContentAsStream(final CourseFile courseFile) {
        return iliasWebClient.getAsInputStream(courseFile.url)
    }

    @Override
    void login(final LoginCredentials loginCredentials) {
        iliasWebClient.login(loginCredentials)
    }

    @Override
    void logout() {
        iliasWebClient.logout()
    }

    @Override
    Collection<Course> getJoinedCourses() {
        return courseSyncService.joinedCourses
    }

    void visit(final Course courseItem, final IliasItemVisitor itemVisitor) {
        courseSyncService.visit(courseItem, itemVisitor)
    }
}
