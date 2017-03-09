package com.github.thetric.iliasdownloader.cli

import groovy.transform.Immutable

import java.nio.file.Path

@Immutable(knownImmutableClasses = [Path])
class CliOptions {
    Path syncDir
    boolean showCourseSelection
}
