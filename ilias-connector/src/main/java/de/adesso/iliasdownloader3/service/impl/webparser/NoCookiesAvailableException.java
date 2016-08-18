package de.adesso.iliasdownloader3.service.impl.webparser;

import de.adesso.iliasdownloader3.service.exception.IliasException;

/**
 * @author broj
 * @since 18.08.2016
 */
public final class NoCookiesAvailableException extends IliasException {
    public NoCookiesAvailableException(String message) {
        super(message);
    }

    public NoCookiesAvailableException(String message, Throwable t) {
        super(message, t);
    }

    public NoCookiesAvailableException(Throwable t) {
        super(t);
    }
}
