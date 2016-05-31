package de.adesso.iliasdownloader3.service;

import de.adesso.iliasdownloader3.service.model.Course;
import de.adesso.iliasdownloader3.service.model.LoginCredentials;

import java.util.Collection;

/**
 * @author broj
 * @since 21.05.2016
 */
public interface IliasService {

    // Session management

    void login(LoginCredentials loginCredentials);

    void logout();

    // course sync

    Collection<Course> getJoinedCourses();
}
