package com.github.thetric.iliasdownloader.service.model

import groovy.transform.CompileStatic
import groovy.transform.Immutable

@Immutable
@CompileStatic
class LoginCredentials {
    String userName, password
}
