package de.adesso.iliasdownloader3.service;

import de.adesso.iliasdownloader3.service.model.LoginCredentials;

/**
 * @author broj
 * @since 21.05.2016
 */
public interface IliasService {
    void login(LoginCredentials loginCredentials);

    void logout();
}
