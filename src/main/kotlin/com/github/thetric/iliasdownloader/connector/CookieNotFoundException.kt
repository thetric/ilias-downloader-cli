package com.github.thetric.iliasdownloader.connector

import com.github.thetric.iliasdownloader.connector.exception.IliasException

/**
 * Thrown if a specified cookie cannot be found in a response.
 */
class CookieNotFoundException(message: String) : IliasException(message)
