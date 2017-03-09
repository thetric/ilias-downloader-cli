package com.github.thetric.iliasdownloader.service.webparser.impl.course.datetime

import java.time.LocalDateTime

interface RelativeDateTimeParser {
    LocalDateTime parse(String dateTimeString)

    boolean isRelativeDateTime(String dateTimeString)
}
