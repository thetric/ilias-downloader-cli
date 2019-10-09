package com.github.thetric.iliasdownloader.connector.api.exception

/**
 * Base class for all Ilias exceptions.
 */
open class IliasException : RuntimeException {
    constructor(message: String) : super(message)

    constructor(message: String, t: Throwable) : super(message, t)

    constructor(t: Throwable) : super(t)
}
