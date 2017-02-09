package com.github.thetric.iliasdownloader.service.webparser.impl

import com.github.thetric.iliasdownloader.service.exception.IliasException
import groovy.transform.CompileStatic

/**
 * @author broj
 * @since 18.08.2016
 */
@CompileStatic
final class IliasItemIdStringParsingException extends IliasException {
    IliasItemIdStringParsingException(String message) {
        super(message)
    }

    IliasItemIdStringParsingException(String message, Throwable t) {
        super(message, t)
    }

    IliasItemIdStringParsingException(Throwable t) {
        super(t)
    }
}
