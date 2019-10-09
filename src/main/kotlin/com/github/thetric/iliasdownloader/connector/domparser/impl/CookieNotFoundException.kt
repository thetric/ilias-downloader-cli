package com.github.thetric.iliasdownloader.connector.domparser.impl

import com.github.thetric.iliasdownloader.connector.api.exception.IliasException

/**
 * Thrown if a specified cookie cannot be found in a response.
 */
class CookieNotFoundException(message: String) : IliasException(message)
