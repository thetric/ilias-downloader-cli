package com.github.thetric.iliasdownloader.service.webparser

import com.github.thetric.iliasdownloader.service.exception.IliasException

/**
 * @author broj
 * @since 18.08.2016
 */
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
