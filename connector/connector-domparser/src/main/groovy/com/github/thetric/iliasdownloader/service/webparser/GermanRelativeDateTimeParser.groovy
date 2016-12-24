package com.github.thetric.iliasdownloader.service.webparser

import groovy.transform.CompileStatic

import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * @author broj
 */
@CompileStatic
final class GermanRelativeDateTimeParser implements RelativeDateTimeParser {
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern('HH:mm', Locale.GERMAN)

    @Override
    LocalDateTime parse(String dateTimeString) {
        Objects.requireNonNull(dateTimeString)

        if (dateTimeString.startsWith('Gestern')) {
            def timeString = dateTimeString.replace('Gestern, ', '')
            def modifiedTime = LocalTime.parse(timeString, timeFormatter)
            return LocalDateTime.now()
                    .withHour(modifiedTime.hour)
                    .withMinute(modifiedTime.minute)
        }
        return null
    }

    @Override
    boolean isRelativeDateTime(String dateTimeString) {
        return !dateTimeString[0].isInteger()
    }
}
