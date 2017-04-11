package com.github.thetric.iliasdownloader.service.webparser.impl

import com.github.thetric.iliasdownloader.service.exception.IliasException
import groovy.transform.CompileStatic

@CompileStatic
final class NoCookiesAvailableException extends IliasException {
    NoCookiesAvailableException(final String message) {
        super(message)
    }

    NoCookiesAvailableException(final String message, final Throwable t) {
        super(message, t)
    }

    NoCookiesAvailableException(final Throwable t) {
        super(t)
    }
}
