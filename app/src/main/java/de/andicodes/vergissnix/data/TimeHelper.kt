package de.andicodes.vergissnix.data

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters

private const val MILLIS_PER_DAY = 1000 * 60 * 60 * 24

object TimeHelper {
    private const val MORNING_HOUR = 9
    private const val NOON_HOUR = 12
    private const val AFTERNOON_HOUR = 15
    private const val EVENING_HOUR = 18

    fun getTimeRecommendations(originalTime: LocalTime?, selectedTime: LocalTime?): Set<LocalTime> {
        val times = sortedSetOf(
            LocalTime.of(MORNING_HOUR, 0),
            LocalTime.of(NOON_HOUR, 0),
            LocalTime.of(AFTERNOON_HOUR, 0),
            LocalTime.of(EVENING_HOUR, 0)
        )
        originalTime?.let { times.add(it.truncatedTo(ChronoUnit.MINUTES)) }
        selectedTime?.let { times.add(it.truncatedTo(ChronoUnit.MINUTES)) }

        return times
    }

    fun getDateRecommendations(
        originalDate: LocalDate?,
        selectedDate: LocalDate?,
        today: LocalDate = LocalDate.now()
    ): Set<LocalDate> {
        val times = sortedSetOf(
            today,
            today.plusDays(1),
            today.with(TemporalAdjusters.next(DayOfWeek.SATURDAY)),
            today.with(TemporalAdjusters.next(DayOfWeek.MONDAY))
        )
        originalDate?.let { times.add(it) }
        selectedDate?.let { times.add(it) }

        return times
    }

    fun localDateOfEpochMillis(epochMillis: Long): LocalDate {
        return LocalDate.ofEpochDay(epochMillis / MILLIS_PER_DAY)
    }
}

fun LocalDate.toEpochMillis(): Long {
    return toEpochDay() * MILLIS_PER_DAY
}

