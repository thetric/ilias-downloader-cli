package com.github.thetric.iliasdownloader.service.model

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * @author broj
 * @since 31.05.2016
 */
@EqualsAndHashCode
@ToString(includeNames = true)
@CompileStatic
final class Course extends AbstractIliasItem {

    private final Collection<? extends CourseItem> items

     Course(int id, String name, String url, Collection<? extends CourseItem> items) {
        super(id, name, url)
        this.items = items
    }

     Course(int id, String name, String url) {
        this(id, name, url, new ArrayList<>())
    }

    Collection<? extends CourseItem> getItems() {
        return items
    }
}
