package com.github.thetric.iliasdownloader.cli

import groovy.transform.Immutable

import java.nio.file.Path

/**
 * Contains the available CLI options.
 *
 * @see CliService
 */
@Immutable(knownImmutableClasses = [Path])
class CliOptions {
    Path syncDir
    boolean showCourseSelection
    Long fileSizeLimitInMiB
}
