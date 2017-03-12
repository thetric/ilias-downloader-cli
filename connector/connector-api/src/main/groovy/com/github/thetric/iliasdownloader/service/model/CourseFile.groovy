package com.github.thetric.iliasdownloader.service.model

import groovy.transform.CompileStatic
import groovy.transform.Immutable
import groovy.transform.ToString

import java.time.LocalDateTime

@ToString(includeNames = true, includeSuperProperties = true)
@CompileStatic
@Immutable(knownImmutableClasses = [LocalDateTime])
class CourseFile implements IliasItem {
    String name
    String url
    IliasItem parent

    LocalDateTime modified
    long size
}
