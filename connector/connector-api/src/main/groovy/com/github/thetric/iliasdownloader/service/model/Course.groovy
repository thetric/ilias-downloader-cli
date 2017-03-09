package com.github.thetric.iliasdownloader.service.model

import groovy.transform.CompileStatic
import groovy.transform.ToString

@ToString(includeNames = true, includeSuperProperties = true)
@CompileStatic
final class Course extends AbstractIliasItem {
    Course(int id, String name, String url) {
        super(id, name, url, null)
    }
}

