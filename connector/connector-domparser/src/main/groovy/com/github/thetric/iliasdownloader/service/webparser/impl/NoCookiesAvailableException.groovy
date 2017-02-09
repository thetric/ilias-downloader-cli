package com.github.thetric.iliasdownloader.service.webparser.impl

import com.github.thetric.iliasdownloader.service.exception.IliasException
import groovy.transform.CompileStatic

/**
 * @author broj
 * @since 18.08.2016
 */
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
