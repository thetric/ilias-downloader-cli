package com.github.thetric.iliasdownloader.service.model

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * @author broj
 * @since 31.05.2016
 */

@EqualsAndHashCode
@ToString(includeNames = true, includeSuperProperties = true)
@CompileStatic
final class Course extends AbstractIliasItem {
    Collection<? extends CourseItem> items

    Course(int id, String name, String url, Collection<? extends CourseItem> items = new ArrayList<>()) {
        super(id, name, url)
        this.items = items
    }
}

