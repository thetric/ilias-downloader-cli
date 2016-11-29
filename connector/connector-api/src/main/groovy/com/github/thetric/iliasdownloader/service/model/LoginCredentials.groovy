package com.github.thetric.iliasdownloader.service.model

import groovy.transform.CompileStatic
import groovy.transform.Immutable

/**
 * Created by Dominik Broj on 01.02.2016.
 *
 * @author Dominik Broj
 * @since 01.02.2016
 */
@Immutable
@CompileStatic
final class LoginCredentials {
    String userName, password
}
