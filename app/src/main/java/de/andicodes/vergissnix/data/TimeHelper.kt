package de.andicodes.vergissnix.data

import de.andicodes.vergissnix.data.TimeRecommendation.RelativeDay
import de.andicodes.vergissnix.data.TimeRecommendation.RelativeTime
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
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

    @JvmStatic
    fun getTimeRecommendations(currentTime: LocalDateTime): List<TimeRecommendation> {
        val timeRecommendationsSameDay = ArrayList<TimeRecommendation>()
        val timeRecommendationsNextDay = ArrayList<TimeRecommendation>()
        val currentTimeAtHour = currentTime.truncatedTo(ChronoUnit.HOURS)
        val date = currentTimeAtHour.toLocalDate()
        val time = currentTimeAtHour.toLocalTime()

        // add recommendations for the next 24 hours
        if (time.isBefore(LocalTime.of(MORNING_HOUR, 0))) {
            timeRecommendationsSameDay.add(
                TimeRecommendation(
                    RelativeDay.SAME_DAY, RelativeTime.MORNING, currentTimeAtHour.withHour(
                        MORNING_HOUR
                    )
                )
            )
        } else {
            timeRecommendationsNextDay.add(
                TimeRecommendation(
                    RelativeDay.NEXT_DAY,
                    RelativeTime.MORNING,
                    currentTimeAtHour.plusDays(1).withHour(
                        MORNING_HOUR
                    )
                )
            )
        }
        if (time.isBefore(LocalTime.of(NOON_HOUR, 0))) {
            timeRecommendationsSameDay.add(
                TimeRecommendation(
                    RelativeDay.SAME_DAY, RelativeTime.NOON, currentTimeAtHour.withHour(
                        NOON_HOUR
                    )
                )
            )
        } else {
            timeRecommendationsNextDay.add(
                TimeRecommendation(
                    RelativeDay.NEXT_DAY, RelativeTime.NOON, currentTimeAtHour.plusDays(1).withHour(
                        NOON_HOUR
                    )
                )
            )
        }
        if (time.isBefore(LocalTime.of(AFTERNOON_HOUR, 0))) {
            timeRecommendationsSameDay.add(
                TimeRecommendation(
                    RelativeDay.SAME_DAY, RelativeTime.AFTERNOON, currentTimeAtHour.withHour(
                        AFTERNOON_HOUR
                    )
                )
            )
        } else {
            timeRecommendationsNextDay.add(
                TimeRecommendation(
                    RelativeDay.NEXT_DAY,
                    RelativeTime.AFTERNOON,
                    currentTimeAtHour.plusDays(1).withHour(
                        AFTERNOON_HOUR
                    )
                )
            )
        }
        if (time.isBefore(LocalTime.of(EVENING_HOUR, 0))) {
            timeRecommendationsSameDay.add(
                TimeRecommendation(
                    RelativeDay.SAME_DAY, RelativeTime.EVENING, currentTimeAtHour.withHour(
                        EVENING_HOUR
                    )
                )
            )
        } else {
            timeRecommendationsNextDay.add(
                TimeRecommendation(
                    RelativeDay.NEXT_DAY,
                    RelativeTime.EVENING,
                    currentTimeAtHour.plusDays(1).withHour(
                        EVENING_HOUR
                    )
                )
            )
        }
        val result = ArrayList<TimeRecommendation>()
        result.addAll(timeRecommendationsSameDay)
        result.addAll(timeRecommendationsNextDay)
        result.add(
            TimeRecommendation(
                RelativeDay.ON_THE_WEEKEND, RelativeTime.MORNING, LocalDateTime.of(
                    date.with(
                        TemporalAdjusters.next(DayOfWeek.SATURDAY)
                    ), LocalTime.of(MORNING_HOUR, 0)
                )
            )
        )
        result.add(
            TimeRecommendation(
                RelativeDay.NEXT_WEEK, RelativeTime.MORNING, LocalDateTime.of(
                    date.with(
                        TemporalAdjusters.next(DayOfWeek.MONDAY)
                    ), LocalTime.of(MORNING_HOUR, 0)
                )
            )
        )
        return result
    }
}