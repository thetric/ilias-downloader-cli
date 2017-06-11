package com.github.thetric.iliasdownloader.service.webparser.impl

import com.github.thetric.iliasdownloader.service.exception.IliasException
import groovy.transform.CompileStatic

/**
 * Thrown if a specified cookie cannot be found in a response.
 */
@CompileStatic
final class CookieNotFoundException extends IliasException {
    CookieNotFoundException(final String message) {
        super(message)
    }
}
