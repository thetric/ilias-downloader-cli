package com.github.thetric.iliasdownloader.service.model

import groovy.transform.CompileStatic
import groovy.transform.Immutable

/**
 * Course folder in an Ilias {@link Course}.
 */
@CompileStatic
@Immutable
class CourseFolder implements IliasItem {
    String name
    String url
    IliasItem parent
}
