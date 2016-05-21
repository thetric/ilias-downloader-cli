package de.adesso.iliasdownloader3.exception;

public final class IliasHttpsException extends IliasException {

    private static final long serialVersionUID = -6541654641065L;

    public IliasHttpsException(String message) {
        super(message);
    }

    public IliasHttpsException(String message, Throwable t) {
        super(message, t);
    }

    public IliasHttpsException(Throwable t) {
        super(t);
    }
}
