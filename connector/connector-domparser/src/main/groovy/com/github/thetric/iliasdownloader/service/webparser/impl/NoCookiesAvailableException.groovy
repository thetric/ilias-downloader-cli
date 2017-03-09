package com.github.thetric.iliasdownloader.service.webparser.impl

import com.github.thetric.iliasdownloader.service.exception.IliasException
import groovy.transform.CompileStatic

@CompileStatic
final class NoCookiesAvailableException extends IliasException {
    NoCookiesAvailableException(String message) {
        super(message)
    }

    NoCookiesAvailableException(String message, Throwable t) {
        super(message, t)
    }

    NoCookiesAvailableException(Throwable t) {
        super(t)
    }
}
