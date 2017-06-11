package com.github.thetric.iliasdownloader.cli

/**
 * @author broj
 * @since 14.01.2017
 */
final class InvalidUsageException extends RuntimeException {
    InvalidUsageException(final String msg) {
        super(msg)
    }

    InvalidUsageException(final String msg, final Throwable cause) {
        super(msg, cause)
    }
}
