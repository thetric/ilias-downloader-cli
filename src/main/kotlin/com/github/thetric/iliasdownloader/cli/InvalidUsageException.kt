package com.github.thetric.iliasdownloader.cli

/**
 * @author broj
 * @since 14.01.2017
 */
internal class InvalidUsageException : RuntimeException {
    constructor(msg: String) : super(msg)

    constructor(msg: String, cause: Throwable) : super(msg, cause)
}
