package com.github.thetric.iliasdownloader.service.model

import groovy.transform.CompileStatic
import groovy.transform.ToString

import java.time.LocalDateTime

@ToString(includeNames = true, includeSuperProperties = true)
@CompileStatic
final class CourseFile extends AbstractIliasItem {
    final LocalDateTime modified
    final long size

    CourseFile(
        final int id,
        final String name, final String url, final IliasItem parent, final LocalDateTime modified, final long size) {
        super(id, name, url, parent)
        this.modified = modified
        this.size = size
    }
}
