package com.github.thetric.iliasdownloader.service.exception

import groovy.transform.CompileStatic;

@CompileStatic
class IliasException extends RuntimeException {

    private static final long serialVersionUID = 168546164616546L;

    IliasException(String message) {
        super(message);
    }

    IliasException(String message, Throwable t) {
        super(message, t);
    }

    IliasException(Throwable t) {
        super(t);
    }
}
