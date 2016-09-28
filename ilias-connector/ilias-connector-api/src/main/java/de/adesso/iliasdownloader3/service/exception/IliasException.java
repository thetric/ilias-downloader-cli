package de.adesso.iliasdownloader3.service.exception;

public class IliasException extends RuntimeException {

    private static final long serialVersionUID = 168546164616546L;

    public IliasException(String message) {
        super(message);
    }

    public IliasException(String message, Throwable t) {
        super(message, t);
    }

    public IliasException(Throwable t) {
        super(t);
    }
}
