package com.github.thetric.iliasdownloader.connector.api

import com.github.thetric.iliasdownloader.connector.api.model.Course
import com.github.thetric.iliasdownloader.connector.api.model.CourseFile
import com.github.thetric.iliasdownloader.connector.api.model.LoginCredentials
import java.io.IOException
import java.io.InputStream

/**
 * Provides access to the Ilias.
 * This interface provides some methods for basic session management (login,
 * logout) and it can list the courses (with/without their contents).
 */
interface IliasService {
    // Session management

    /**
     * Logs the user in.
     *
     * @param loginCredentials user credentials
     * @see [logout]
     */
    fun login(loginCredentials: LoginCredentials)

    /**
     * Logs the current user out.
     *
     * @see [login]
     */
    fun logout()

    // course sync

    /**
     * Finds all courses without any course content.
     *
     * @return all [Course]s of the current user
     * @see [visit]
     */
    fun getJoinedCourses(): Collection<Course>

    fun <C> visit(
        courseItem: Course,
        itemVisitor: ContextAwareIliasItemVisitor<C>,
        initialContext: C
    )

    /**
     * Downloads the content of the {@link CourseFile} from the Ilias.
     *
     * @param file [CourseFile] to download
     * @return [InputStream] content of the file
     * @see com.github.thetric.iliasdownloader.connector.api.exception.IliasException
     */
    @Throws(IOException::class)
    fun getContentAsStream(file: CourseFile): InputStream
}
