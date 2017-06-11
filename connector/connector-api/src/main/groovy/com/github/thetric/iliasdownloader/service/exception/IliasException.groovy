package com.github.thetric.iliasdownloader.service.exception

import groovy.transform.CompileStatic

/**
 * Base class for all Ilias exceptions.
 */
@CompileStatic
class IliasException extends RuntimeException {
    private static final long serialVersionUID = 168546164616546L

    IliasException(final String message) {
        super(message)
    }

    IliasException(final String message, final Throwable t) {
        super(message, t)
    }

    IliasException(final Throwable t) {
        super(t)
    }
}
