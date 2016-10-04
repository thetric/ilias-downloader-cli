package com.github.thetric.iliasdownloader.service.webparser;

import com.github.thetric.iliasdownloader.service.exception.IliasException;

/**
 * @author broj
 * @since 18.08.2016
 */
public final class IliasItemIdStringParsingException extends IliasException {
    public IliasItemIdStringParsingException(String message) {
        super(message);
    }

    public IliasItemIdStringParsingException(String message, Throwable t) {
        super(message, t);
    }

    public IliasItemIdStringParsingException(Throwable t) {
        super(t);
    }
}
