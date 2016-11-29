package com.github.thetric.iliasdownloader.service.model

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * @author broj
 * @since 18.08.2016
 */
@EqualsAndHashCode
@ToString(includeNames = true)
@CompileStatic
final class CourseFolder extends AbstractIliasItem implements CourseItem {
    final Collection<? extends CourseItem> courseItems

    CourseFolder(int id, String name, String url, Collection<? extends CourseItem> courseItems) {
        super(id, name, url)
        this.courseItems = courseItems
    }
}
