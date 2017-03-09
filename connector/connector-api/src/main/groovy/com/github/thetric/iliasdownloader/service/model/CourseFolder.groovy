package com.github.thetric.iliasdownloader.service.model

import groovy.transform.CompileStatic
import groovy.transform.ToString

@ToString(includeNames = true, includeSuperProperties = true)
@CompileStatic
final class CourseFolder extends AbstractIliasItem {
    CourseFolder(int id, String name, String url, IliasItem parent) {
        super(id, name, url, parent)
    }
}
