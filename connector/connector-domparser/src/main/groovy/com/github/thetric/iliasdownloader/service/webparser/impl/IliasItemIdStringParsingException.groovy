package com.github.thetric.iliasdownloader.service.webparser.impl

import com.github.thetric.iliasdownloader.service.exception.IliasException
import groovy.transform.CompileStatic

@CompileStatic
final class IliasItemIdStringParsingException extends IliasException {
    IliasItemIdStringParsingException(final String message) {
        super(message)
    }

    IliasItemIdStringParsingException(final String message, final Throwable t) {
        super(message, t)
    }

    IliasItemIdStringParsingException(final Throwable t) {
        super(t)
    }
}
