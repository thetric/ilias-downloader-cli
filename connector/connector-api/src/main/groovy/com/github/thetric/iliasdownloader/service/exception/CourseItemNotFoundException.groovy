package com.github.thetric.iliasdownloader.service.exception

import groovy.transform.CompileStatic

/**
 * Indicates a non existing Ilias item.
 */
@CompileStatic
final class CourseItemNotFoundException extends IliasException {
    private static final long serialVersionUID = 654654651649684L
    final String url

    CourseItemNotFoundException(final String message, final String url) {
        super(message + url)
        this.url = url
    }
}
