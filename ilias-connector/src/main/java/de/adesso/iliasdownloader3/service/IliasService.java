package de.adesso.iliasdownloader3.service;

import de.adesso.iliasdownloader3.service.model.Course;
import de.adesso.iliasdownloader3.service.model.LoginCredentials;

import java.util.Collection;

/**
 * Provides access to the Ilias.
 * This interface provides some methods for basic session management (login, logout) and it can list the courses
 * (with/without their contents).
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
     * @see #searchCoursesWithContent()
     */
    Collection<Course> getJoinedCourses();
}
