package com.github.thetric.iliasdownloader.service.model

import groovy.transform.CompileStatic
import groovy.transform.ToString

import java.time.LocalDateTime

@ToString(includeNames = true, includeSuperProperties = true)
@CompileStatic
final class CourseFile extends AbstractIliasItem {
    LocalDateTime modified

    CourseFile(int id, String name, String url, IliasItem parent, LocalDateTime modified) {
        super(id, name, url, parent)
        this.modified = modified
    }
}
