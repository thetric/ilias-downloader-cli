package com.github.thetric.iliasdownloader.connector

import com.github.thetric.iliasdownloader.connector.exception.IliasException

/**
 * Indicates an error when parsing an Ilias item ID string.
 */
class IliasItemIdStringParsingException(message: String, t: Throwable) : IliasException(message, t)
