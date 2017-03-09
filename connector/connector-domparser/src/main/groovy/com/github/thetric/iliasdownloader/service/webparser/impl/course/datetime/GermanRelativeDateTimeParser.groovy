package com.github.thetric.iliasdownloader.service.webparser.impl.course.datetime

import groovy.transform.CompileStatic

import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@CompileStatic
final class GermanRelativeDateTimeParser implements RelativeDateTimeParser {
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern('HH:mm', Locale.GERMAN)

    @Override
    LocalDateTime parse(String dateTimeString) {
        Objects.requireNonNull(dateTimeString)

        if (dateTimeString.startsWith('Heute')) {
            LocalDateTime today = LocalDateTime.now()
            return parseRelativeDateTime(dateTimeString, 'Heute', today)
        }
        if (dateTimeString.startsWith('Gestern')) {
            LocalDateTime yesterday = LocalDateTime.now().minusDays(1L)
            return parseRelativeDateTime(dateTimeString, 'Gestern', yesterday)
        }
        throw new IllegalArgumentException("Cannot parse date time: $dateTimeString")
    }

    private LocalDateTime parseRelativeDateTime(String dateTimeString, String prefix, LocalDateTime baseDay) {
        def timeString = dateTimeString.replace("$prefix, ", '')
        def modifiedTime = LocalTime.parse(timeString, timeFormatter)
        return baseDay.withHour(modifiedTime.hour)
                      .withMinute(modifiedTime.minute)
                      .withSecond(0)
                      .withNano(0)
    }

    @Override
    boolean isRelativeDateTime(String dateTimeString) {
        return !dateTimeString[0].isInteger()
    }
}
