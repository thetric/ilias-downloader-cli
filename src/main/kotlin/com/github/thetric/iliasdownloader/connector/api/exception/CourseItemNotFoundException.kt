package com.github.thetric.iliasdownloader.connector.api.exception

/**
 * Indicates a non existing Ilias item.
 */
class CourseItemNotFoundException(message: String, url: String) :
    IliasException(message + url)
