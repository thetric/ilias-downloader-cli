package de.adesso.iliasdownloader3.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum LoginType {
	DEFAULT("login"), LDAP("loginLDAP"), CAS("loginCAS");

	private final String loginMethodName;
}
