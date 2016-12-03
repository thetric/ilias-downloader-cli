package com.github.thetric.iliasdownloader.service.exception

import groovy.transform.CompileStatic

@CompileStatic
final class IliasAuthenticationException extends IliasException {
    private static final long serialVersionUID = 965016465165430L

    IliasAuthenticationException(String message) {
        super(message)
    }

    IliasAuthenticationException(String message, Throwable t) {
        super(message, t)
    }

    IliasAuthenticationException(Throwable t) {
        super(t)
    }
}
