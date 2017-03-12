package com.github.thetric.iliasdownloader.service.model

import groovy.transform.CompileStatic
import groovy.transform.Immutable

@CompileStatic
@Immutable
class CourseFolder implements IliasItem {
    String name
    String url
    IliasItem parent
}
