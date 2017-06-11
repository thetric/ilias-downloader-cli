package com.github.thetric.iliasdownloader.service.model

import groovy.transform.CompileStatic
import groovy.transform.Immutable

/**
 * Represents the login credentials necessary for an Ilias login.
 *
 * @see com.github.thetric.iliasdownloader.service.IliasService
 */
@Immutable
@CompileStatic
class LoginCredentials {
    String userName, password
}
