package de.adesso.iliasdownloader3.service.exception;

public final class IliasAuthenticationException extends IliasException {

    private static final long serialVersionUID = 965016465165430L;

    public IliasAuthenticationException(String message) {
        super(message);
    }

    public IliasAuthenticationException(String message, Throwable t) {
        super(message, t);
    }

    public IliasAuthenticationException(Throwable t) {
        super(t);
    }
}
