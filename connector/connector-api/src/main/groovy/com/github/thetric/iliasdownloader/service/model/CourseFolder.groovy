package com.github.thetric.iliasdownloader.service.model

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * @author broj
 * @since 18.08.2016
 */
@EqualsAndHashCode
@ToString(includeNames = true, includeSuperProperties = true)
@CompileStatic
final class CourseFolder extends AbstractIliasItem {
    CourseFolder(int id, String name, String url, IliasItem parent) {
        super(id, name, url, parent)
    }
}
