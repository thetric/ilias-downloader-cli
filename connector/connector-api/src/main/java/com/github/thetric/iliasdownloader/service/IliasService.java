package com.github.thetric.iliasdownloader.service;

import com.github.thetric.iliasdownloader.service.model.Course;
import com.github.thetric.iliasdownloader.service.model.CourseFile;
import com.github.thetric.iliasdownloader.service.model.CourseItem;
import com.github.thetric.iliasdownloader.service.model.LoginCredentials;
import io.reactivex.Observable;
import io.reactivex.Single;

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
     *         {@link Course}s to search for. The list itself and its items <i>must not</i> be modified under any
     *         circumstances
     * @return new list with {@link Course}s and their child nodes
     * @see CourseItem
     */
    Observable<Course> searchCoursesWithContent(Collection<Course> selectedCourses);

    /**
     * Downloads the content of the {@link CourseFile} from the Ilias.
     *
     * @param file
     *         {@link CourseFile} to download
     * @return {@link Single} which publishes either the data as a byte array or an exception
     * @see java.io.IOException
     * @see com.github.thetric.iliasdownloader.service.exception.IliasException
     */
    Single<byte[]> getContent(CourseFile file);
}
