package de.andicodes.vergissnix.data

import de.andicodes.vergissnix.data.TimeHelper.getTimeRecommendations
import de.andicodes.vergissnix.data.TimeRecommendation.RelativeDay
import de.andicodes.vergissnix.data.TimeRecommendation.RelativeTime
import org.junit.Assert
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

class TimeHelperTest {
    @Test
    fun TimeRecommendations_Next24Hours_From7() {
        // Thursday, 2021-11-10
        val date = LocalDate.of(2021, 11, 10)
        val time = LocalTime.of(7, 1)
        val startTime = LocalDateTime.of(date, time)
        val expected = Arrays.asList(
            TimeRecommendation(RelativeDay.SAME_DAY, RelativeTime.MORNING, dateTime(date, 9)),
            TimeRecommendation(RelativeDay.SAME_DAY, RelativeTime.NOON, dateTime(date, 12)),
            TimeRecommendation(RelativeDay.SAME_DAY, RelativeTime.AFTERNOON, dateTime(date, 15)),
            TimeRecommendation(RelativeDay.SAME_DAY, RelativeTime.EVENING, dateTime(date, 18)),
            TimeRecommendation(
                RelativeDay.ON_THE_WEEKEND,
                RelativeTime.MORNING,
                dateTime(LocalDate.of(2021, 11, 13), 9)
            ),
            TimeRecommendation(
                RelativeDay.NEXT_WEEK,
                RelativeTime.MORNING,
                dateTime(LocalDate.of(2021, 11, 15), 9)
            )
        )
        val actual = getTimeRecommendations(startTime)
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun TimeRecommendations_Next24Hours_From10() {
        // Thursday, 2021-11-10
        val date = LocalDate.of(2021, 11, 10)
        val time = LocalTime.of(10, 15)
        val startTime = LocalDateTime.of(date, time)
        val expected = Arrays.asList(
            TimeRecommendation(RelativeDay.SAME_DAY, RelativeTime.NOON, dateTime(date, 12)),
            TimeRecommendation(RelativeDay.SAME_DAY, RelativeTime.AFTERNOON, dateTime(date, 15)),
            TimeRecommendation(RelativeDay.SAME_DAY, RelativeTime.EVENING, dateTime(date, 18)),
            TimeRecommendation(
                RelativeDay.NEXT_DAY,
                RelativeTime.MORNING,
                dateTime(date.plusDays(1), 9)
            ),
            TimeRecommendation(
                RelativeDay.ON_THE_WEEKEND,
                RelativeTime.MORNING,
                dateTime(LocalDate.of(2021, 11, 13), 9)
            ),
            TimeRecommendation(
                RelativeDay.NEXT_WEEK,
                RelativeTime.MORNING,
                dateTime(LocalDate.of(2021, 11, 15), 9)
            )
        )
        val actual = getTimeRecommendations(startTime)
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun TimeRecommendations_Next24Hours_From13() {
        // Thursday, 2021-11-10
        val date = LocalDate.of(2021, 11, 10)
        val time = LocalTime.of(13, 22)
        val startTime = LocalDateTime.of(date, time)
        val expected = Arrays.asList(
            TimeRecommendation(RelativeDay.SAME_DAY, RelativeTime.AFTERNOON, dateTime(date, 15)),
            TimeRecommendation(RelativeDay.SAME_DAY, RelativeTime.EVENING, dateTime(date, 18)),
            TimeRecommendation(
                RelativeDay.NEXT_DAY,
                RelativeTime.MORNING,
                dateTime(date.plusDays(1), 9)
            ),
            TimeRecommendation(
                RelativeDay.NEXT_DAY,
                RelativeTime.NOON,
                dateTime(date.plusDays(1), 12)
            ),
            TimeRecommendation(
                RelativeDay.ON_THE_WEEKEND,
                RelativeTime.MORNING,
                dateTime(LocalDate.of(2021, 11, 13), 9)
            ),
            TimeRecommendation(
                RelativeDay.NEXT_WEEK,
                RelativeTime.MORNING,
                dateTime(LocalDate.of(2021, 11, 15), 9)
            )
        )
        val actual = getTimeRecommendations(startTime)
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun TimeRecommendations_Next24Hours_From17() {
        // Thursday, 2021-11-10
        val date = LocalDate.of(2021, 11, 10)
        val time = LocalTime.of(17, 55)
        val startTime = LocalDateTime.of(date, time)
        val expected = Arrays.asList(
            TimeRecommendation(RelativeDay.SAME_DAY, RelativeTime.EVENING, dateTime(date, 18)),
            TimeRecommendation(
                RelativeDay.NEXT_DAY,
                RelativeTime.MORNING,
                dateTime(date.plusDays(1), 9)
            ),
            TimeRecommendation(
                RelativeDay.NEXT_DAY,
                RelativeTime.NOON,
                dateTime(date.plusDays(1), 12)
            ),
            TimeRecommendation(
                RelativeDay.NEXT_DAY,
                RelativeTime.AFTERNOON,
                dateTime(date.plusDays(1), 15)
            ),
            TimeRecommendation(
                RelativeDay.ON_THE_WEEKEND,
                RelativeTime.MORNING,
                dateTime(LocalDate.of(2021, 11, 13), 9)
            ),
            TimeRecommendation(
                RelativeDay.NEXT_WEEK,
                RelativeTime.MORNING,
                dateTime(LocalDate.of(2021, 11, 15), 9)
            )
        )
        val actual = getTimeRecommendations(startTime)
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun TimeRecommendations_Next24Hours_From20() {
        // Thursday, 2021-11-10
        val date = LocalDate.of(2021, 11, 10)
        val time = LocalTime.of(20, 39)
        val startTime = LocalDateTime.of(date, time)
        val expected = Arrays.asList(
            TimeRecommendation(
                RelativeDay.NEXT_DAY,
                RelativeTime.MORNING,
                dateTime(date.plusDays(1), 9)
            ),
            TimeRecommendation(
                RelativeDay.NEXT_DAY,
                RelativeTime.NOON,
                dateTime(date.plusDays(1), 12)
            ),
            TimeRecommendation(
                RelativeDay.NEXT_DAY,
                RelativeTime.AFTERNOON,
                dateTime(date.plusDays(1), 15)
            ),
            TimeRecommendation(
                RelativeDay.NEXT_DAY,
                RelativeTime.EVENING,
                dateTime(date.plusDays(1), 18)
            ),
            TimeRecommendation(
                RelativeDay.ON_THE_WEEKEND,
                RelativeTime.MORNING,
                dateTime(LocalDate.of(2021, 11, 13), 9)
            ),
            TimeRecommendation(
                RelativeDay.NEXT_WEEK,
                RelativeTime.MORNING,
                dateTime(LocalDate.of(2021, 11, 15), 9)
            )
        )
        val actual = getTimeRecommendations(startTime)
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun TimeRecommendations_ContainsUpcomingWeekendAndNextWeek() {
        // Monday, 2021-10-18
        val date = LocalDate.of(2021, 10, 18)
        val time = LocalTime.of(13, 39)
        val startTime = LocalDateTime.of(date, time)
        val onWeekend = TimeRecommendation(
            RelativeDay.ON_THE_WEEKEND, RelativeTime.MORNING, dateTime(
                LocalDate.of(2021, 10, 23), 9
            )
        )
        val nextWeek = TimeRecommendation(
            RelativeDay.NEXT_WEEK, RelativeTime.MORNING, dateTime(
                LocalDate.of(2021, 10, 25), 9
            )
        )
        val actual = getTimeRecommendations(startTime)
        Assert.assertEquals(onWeekend, actual[4])
        Assert.assertEquals(nextWeek, actual[5])
    }

    @Test
    fun TimeRecommendations_IsSorted() {
        // Monday, 2021-10-18
        val date = LocalDate.of(2021, 10, 18)
        val time = LocalTime.of(13, 39)
        val startTime = LocalDateTime.of(date, time)
        val actual = getTimeRecommendations(startTime)
        val sorted = ArrayList(actual)
        sorted.sortWith(Comparator.comparing(TimeRecommendation::dateTime))
        Assert.assertEquals(sorted, actual)
    }

    private fun dateTime(date: LocalDate, hour: Int): LocalDateTime {
        return LocalDateTime.of(date, LocalTime.of(hour, 0))
    }
}