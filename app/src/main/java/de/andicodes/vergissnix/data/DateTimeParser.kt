package de.andicodes.vergissnix.data

import java.time.DateTimeException
import java.time.LocalDateTime
import java.util.regex.Pattern

class DateTimeParser(private val now: LocalDateTime = LocalDateTime.now()) {

    private val absoluteTimePattern: Pattern = Pattern.compile("([012]?[0-9])[.:]([0-6][0-9])")
    private val absoluteDatePattern: Pattern = Pattern.compile("([0123]?[0-9])[./]([01]?[0-9])[./](2?0?[0-9][0-9])?")

    fun parse(text: String): List<LocalDateTime> {
        val timeMatcher = absoluteTimePattern.matcher(text)

        if (timeMatcher.find()) {
            val hour = timeMatcher.group(1)?.toIntOrNull()
            val minute = timeMatcher.group(2)?.toIntOrNull()
            if (hour != null && minute != null) {
                return try {
                    listOf(now.withHour(hour).withMinute(minute).withSecond(0))
                } catch (e: DateTimeException) {
                    emptyList()
                }
            }
        }

        val dateMatcher = absoluteDatePattern.matcher(text)

        if (dateMatcher.find()) {
            val day = timeMatcher.group(1)?.toIntOrNull()
            val month = timeMatcher.group(2)?.toIntOrNull()
            val year = timeMatcher.group(3)?.toIntOrNull()
            if (day != null && month != null && year != null) {
                return try {
                    listOf(now.withDayOfMonth(day).withMonth(month).withYear(year))
                } catch (e: DateTimeException) {
                    emptyList()
                }
            }
        }

        return emptyList()
    }
}
