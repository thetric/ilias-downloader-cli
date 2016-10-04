package de.adesso.iliasdownloader3.service;

import de.adesso.iliasdownloader3.service.model.Course;
import de.adesso.iliasdownloader3.service.model.LoginCredentials;
import io.reactivex.Observable;

import java.util.Collection;

/**
 * Provides access to the Ilias.
 * This interface provides some methods for basic session management (login, logout) and it can list the courses
 * (with/without their contents).
 *
 * @author broj
 * @since 21.05.2016
 */
public interface IliasService {

    // Session management

    /**
     * Logs the user in.
     *
     * @param loginCredentials
     *         user credentials
     * @see #logout()
     */
    void login(LoginCredentials loginCredentials);

    /**
     * Logs the current user out.
     *
     * @see #login(LoginCredentials)
     */
    void logout();

    // course sync

    /**
     * Finds all courses without any course content.
     *
     * @return all courses of the current user
     * @see #searchCoursesWithContent(Collection)
     */
    Observable<Course> getJoinedCourses();

    /**
     * Searches the selected courses with their child nodes <b>without downloading them</b>.
     *
     * @param selectedCourses
     *         {@link Course}s to search for, must not be modified by this method
     * @return new list with {@link Course}s and their child nodes
     * @see de.adesso.iliasdownloader3.service.model.CourseItem
     */
    Observable<Course> searchCoursesWithContent(Collection<Course> selectedCourses);
}
