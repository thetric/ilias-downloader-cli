package com.github.thetric.iliasdownloader.service.model

import groovy.transform.CompileStatic
import groovy.transform.Immutable
import groovy.transform.ToString

@ToString(includeNames = true, includeSuperProperties = true)
@CompileStatic
@Immutable
class Course implements IliasItem {
    int id
    String name
    String url
    // for consistency with CourseFile, CourseFolder
    IliasItem parent = null
}

