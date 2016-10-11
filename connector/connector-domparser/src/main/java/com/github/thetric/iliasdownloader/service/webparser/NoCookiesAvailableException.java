package com.github.thetric.iliasdownloader.service.webparser;

import com.github.thetric.iliasdownloader.service.exception.IliasException;

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
