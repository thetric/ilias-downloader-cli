package de.adesso.iliasdownloader3.service.model;

import lombok.Value;

/**
 * Created by Dominik Broj on 01.02.2016.
 *
 * @author Dominik Broj
 * @since 01.02.2016
 */
@Value
public final class LoginCredentials {
    private final String userName, password;
}
