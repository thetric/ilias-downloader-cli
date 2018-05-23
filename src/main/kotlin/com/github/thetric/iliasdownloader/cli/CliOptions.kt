package com.github.thetric.iliasdownloader.cli

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

/**
 * Contains the available CLI options.
 */
internal class CliOptions(parser: ArgParser, resourceBundle: ResourceBundle) {
    val syncDir: Path by parser.storing(
        "-d", "--dir",
        help = resourceBundle.getString("args.directory")
    ) { Paths.get(this) }
    val showCourseSelection: Boolean by parser.flagging(
        "-c", "--select-courses",
        help = resourceBundle.getString("args.course.selection")
    ).default(false)
    val fileSizeLimitInMiB: Long? by parser.storing(
        "-s", "--max-size",
        help = resourceBundle.getString("args.sync.max-size")
    ) { toLong() }.default(null as Long?)

    init {
        parser.force()
    }
}
