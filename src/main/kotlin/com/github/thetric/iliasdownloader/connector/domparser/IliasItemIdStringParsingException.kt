package com.github.thetric.iliasdownloader.connector.domparser

import com.github.thetric.iliasdownloader.connector.api.exception.IliasException

/**
 * Indicates an error when parsing an Ilias item ID string.
 */
class IliasItemIdStringParsingException(message: String, t: Throwable) : IliasException(message, t)
