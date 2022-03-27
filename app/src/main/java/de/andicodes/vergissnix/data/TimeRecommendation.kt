package de.andicodes.vergissnix.data

import java.time.LocalDateTime

data class TimeRecommendation(
    val relativeDay: RelativeDay,
    val relativeTime: RelativeTime,
    val dateTime: LocalDateTime
) {
    enum class RelativeDay {
        SAME_DAY, NEXT_DAY, ON_THE_WEEKEND, NEXT_WEEK
    }

    enum class RelativeTime {
        MORNING, NOON, AFTERNOON, EVENING
    }
}