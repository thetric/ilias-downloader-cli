package com.github.thetric.iliasdownloader.service.soap.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum LoginType {
    DEFAULT("login"), LDAP("loginLDAP"), CAS("loginCAS");

    private final String loginMethodName;
}
