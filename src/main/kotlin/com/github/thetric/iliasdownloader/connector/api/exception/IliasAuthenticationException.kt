package com.github.thetric.iliasdownloader.connector.api.exception

/**
 * Authentication at the Ilias web service failed.
 */
class IliasAuthenticationException : IliasException {
    constructor(message: String) : super(message)

    constructor(message: String, t: Throwable) : super(message, t)

    constructor(t: Throwable) : super(t)
}
