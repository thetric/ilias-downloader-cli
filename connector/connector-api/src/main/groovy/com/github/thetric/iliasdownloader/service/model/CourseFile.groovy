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
 final class CourseFile extends AbstractIliasItem implements CourseItem {
     CourseFile(int id, String name, String url) {
        super(id, name, url)
    }
}
