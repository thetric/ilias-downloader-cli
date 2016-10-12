package com.github.thetric.iliasdownloader.service.soap.model

import groovy.transform.CompileStatic

@CompileStatic
enum LoginType {
    DEFAULT('login'), LDAP('loginLDAP'), CAS('loginCAS')

    String loginMethodName

    LoginType(String loginMethodName) {
        this.loginMethodName = loginMethodName
    }
}
