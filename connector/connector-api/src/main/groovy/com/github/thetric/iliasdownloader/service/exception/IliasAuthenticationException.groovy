package com.github.thetric.iliasdownloader.service.exception

import groovy.transform.CompileStatic

@CompileStatic
final class IliasAuthenticationException extends IliasException {
    private static final long serialVersionUID = 965016465165430L

    IliasAuthenticationException(final String message) {
        super(message)
    }

    IliasAuthenticationException(final String message, final Throwable t) {
        super(message, t)
    }

    IliasAuthenticationException(final Throwable t) {
        super(t)
    }
}
