package de.andicodes.vergissnix.data

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters

object TimeHelper {
    private const val MORNING_HOUR = 9
    private const val NOON_HOUR = 12
    private const val AFTERNOON_HOUR = 15
    private const val EVENING_HOUR = 18

    fun getTimeRecommendations(originalTime: LocalTime?): Set<LocalTime> {
        val times = sortedSetOf(
            LocalTime.of(MORNING_HOUR, 0),
            LocalTime.of(NOON_HOUR, 0),
            LocalTime.of(AFTERNOON_HOUR, 0),
            LocalTime.of(EVENING_HOUR, 0)
        )
        originalTime?.let { times.add(it.truncatedTo(ChronoUnit.MINUTES)) }

        return times
    }

    fun getDateRecommendations(
        originalDate: LocalDate?,
        today: LocalDate = LocalDate.now()
    ): Set<LocalDate> {
        val times = sortedSetOf(
            today,
            today.plusDays(1),
            today.with(TemporalAdjusters.next(DayOfWeek.SATURDAY)),
            today.with(TemporalAdjusters.next(DayOfWeek.MONDAY))
        )
        originalDate?.let { times.add(it) }

        return times
    }
}