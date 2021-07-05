package com.github.thetric.iliasdownloader.connector.exception

/**
 * Thrown if a specified cookie cannot be found in a response.
 */
class CookieNotFoundException(message: String) : IliasException(message)
