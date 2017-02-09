package com.github.thetric.iliasdownloader.service.webparser.impl.course.datetime

import java.time.LocalDateTime

/**
 * @author broj
 */
interface RelativeDateTimeParser {
    LocalDateTime parse(String dateTimeString)

    boolean isRelativeDateTime(String dateTimeString)
}
