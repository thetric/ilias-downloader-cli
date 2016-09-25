package de.adesso.iliasdownloader3.service.impl.webparser;

import de.adesso.iliasdownloader3.service.exception.IliasException;

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
