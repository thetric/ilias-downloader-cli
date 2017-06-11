package com.github.thetric.iliasdownloader.service.model

import groovy.transform.CompileStatic

/**
 * Base interface for all Ilias items.
 *
 * @see Course
 * @see CourseFile
 * @see CourseFolder
 */
@CompileStatic
interface IliasItem {
    String getName()

    String getUrl()

    IliasItem getParent()
}
