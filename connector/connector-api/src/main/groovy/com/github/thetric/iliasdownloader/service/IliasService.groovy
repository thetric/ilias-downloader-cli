package com.github.thetric.iliasdownloader.service

import com.github.thetric.iliasdownloader.service.model.Course
import com.github.thetric.iliasdownloader.service.model.CourseFile
import com.github.thetric.iliasdownloader.service.model.CourseItem
import com.github.thetric.iliasdownloader.service.model.LoginCredentials
import groovy.transform.CompileStatic

/**
 * Provides access to the Ilias.
 * This interface provides some methods for basic session management (login, logout) and it can list the courses
 * (with/without their contents).
 *
 * @author broj
 * @since 21.05.2016
 */
@CompileStatic
interface IliasService {
    enum VisitResult {
        CONTINUE, TERMINATE
    }

    // Session management

    /**
     * Logs the user in.
     *
     * @param loginCredentials
     *         user credentials
     * @see #logout()
     */
    void login(LoginCredentials loginCredentials)

    /**
     * Logs the current user out.
     *
     * @see #login(LoginCredentials)
     */
    void logout()

    // course sync

    /**
     * Finds all courses without any course content.
     *
     * @return all courses of the current user
     * @see #visit(com.github.thetric.iliasdownloader.service.model.CourseItem, groovy.lang.Closure)
     */
    Collection<Course> getJoinedCourses()

    void visit(CourseItem courseItem, Closure<VisitResult> visitMethod)

    /**
     * Downloads the content of the {@link CourseFile} from the Ilias.
     *
     * @param file
     * {@link CourseFile} to download
     * @return {@link InputStream} content of the file
     * @see java.io.IOException
     * @see com.github.thetric.iliasdownloader.service.exception.IliasException
     */
    InputStream getContentAsStream(CourseFile file)
}
