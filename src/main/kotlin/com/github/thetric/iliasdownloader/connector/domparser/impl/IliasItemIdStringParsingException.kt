package com.github.thetric.iliasdownloader.connector.domparser.impl

import com.github.thetric.iliasdownloader.connector.api.exception.IliasException

/**
 * Indicates an error when parsing an Ilias item ID string.
 */
class IliasItemIdStringParsingException : IliasException {
    constructor(message: String) : super(message)

    constructor(message: String, t: Throwable) : super(message, t)
}
