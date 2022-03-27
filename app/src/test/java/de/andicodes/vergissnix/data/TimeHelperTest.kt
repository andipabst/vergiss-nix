package de.andicodes.vergissnix.data;

import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import static de.andicodes.vergissnix.data.TimeRecommendation.RelativeDay.*;
import static de.andicodes.vergissnix.data.TimeRecommendation.RelativeTime.*;
import static org.junit.Assert.assertEquals;

public class TimeHelperTest {

    @Test
    public void getTimeRecommendations_Next24Hours_From7() {
        // Thursday, 2021-11-10
        var date = LocalDate.of(2021, 11, 10);
        var time = LocalTime.of(7, 1);
        var startTime = LocalDateTime.of(date, time);

        var expected = Arrays.asList(
                new TimeRecommendation(SAME_DAY, MORNING, dateTime(date, 9)),
                new TimeRecommendation(SAME_DAY, NOON, dateTime(date, 12)),
                new TimeRecommendation(SAME_DAY, AFTERNOON, dateTime(date, 15)),
                new TimeRecommendation(SAME_DAY, EVENING, dateTime(date, 18)),
                new TimeRecommendation(ON_THE_WEEKEND, MORNING, dateTime(LocalDate.of(2021, 11, 13), 9)),
                new TimeRecommendation(NEXT_WEEK, MORNING, dateTime(LocalDate.of(2021, 11, 15), 9))
        );

        var actual = TimeHelper.getTimeRecommendations(startTime);
        assertEquals(expected, actual);
    }

    @Test
    public void getTimeRecommendations_Next24Hours_From10() {
        // Thursday, 2021-11-10
        var date = LocalDate.of(2021, 11, 10);
        var time = LocalTime.of(10, 15);
        var startTime = LocalDateTime.of(date, time);

        var expected = Arrays.asList(
                new TimeRecommendation(SAME_DAY, NOON, dateTime(date, 12)),
                new TimeRecommendation(SAME_DAY, AFTERNOON, dateTime(date, 15)),
                new TimeRecommendation(SAME_DAY, EVENING, dateTime(date, 18)),
                new TimeRecommendation(NEXT_DAY, MORNING, dateTime(date.plusDays(1), 9)),
                new TimeRecommendation(ON_THE_WEEKEND, MORNING, dateTime(LocalDate.of(2021, 11, 13), 9)),
                new TimeRecommendation(NEXT_WEEK, MORNING, dateTime(LocalDate.of(2021, 11, 15), 9))
        );

        var actual = TimeHelper.getTimeRecommendations(startTime);
        assertEquals(expected, actual);
    }

    @Test
    public void getTimeRecommendations_Next24Hours_From13() {
        // Thursday, 2021-11-10
        var date = LocalDate.of(2021, 11, 10);
        var time = LocalTime.of(13, 22);
        var startTime = LocalDateTime.of(date, time);

        var expected = Arrays.asList(
                new TimeRecommendation(SAME_DAY, AFTERNOON, dateTime(date, 15)),
                new TimeRecommendation(SAME_DAY, EVENING, dateTime(date, 18)),
                new TimeRecommendation(NEXT_DAY, MORNING, dateTime(date.plusDays(1), 9)),
                new TimeRecommendation(NEXT_DAY, NOON, dateTime(date.plusDays(1), 12)),
                new TimeRecommendation(ON_THE_WEEKEND, MORNING, dateTime(LocalDate.of(2021, 11, 13), 9)),
                new TimeRecommendation(NEXT_WEEK, MORNING, dateTime(LocalDate.of(2021, 11, 15), 9))
        );

        var actual = TimeHelper.getTimeRecommendations(startTime);
        assertEquals(expected, actual);
    }

    @Test
    public void getTimeRecommendations_Next24Hours_From17() {
        // Thursday, 2021-11-10
        var date = LocalDate.of(2021, 11, 10);
        var time = LocalTime.of(17, 55);
        var startTime = LocalDateTime.of(date, time);

        var expected = Arrays.asList(
                new TimeRecommendation(SAME_DAY, EVENING, dateTime(date, 18)),
                new TimeRecommendation(NEXT_DAY, MORNING, dateTime(date.plusDays(1), 9)),
                new TimeRecommendation(NEXT_DAY, NOON, dateTime(date.plusDays(1), 12)),
                new TimeRecommendation(NEXT_DAY, AFTERNOON, dateTime(date.plusDays(1), 15)),
                new TimeRecommendation(ON_THE_WEEKEND, MORNING, dateTime(LocalDate.of(2021, 11, 13), 9)),
                new TimeRecommendation(NEXT_WEEK, MORNING, dateTime(LocalDate.of(2021, 11, 15), 9))
        );

        var actual = TimeHelper.getTimeRecommendations(startTime);
        assertEquals(expected, actual);
    }

    @Test
    public void getTimeRecommendations_Next24Hours_From20() {
        // Thursday, 2021-11-10
        var date = LocalDate.of(2021, 11, 10);
        var time = LocalTime.of(20, 39);
        var startTime = LocalDateTime.of(date, time);

        var expected = Arrays.asList(
                new TimeRecommendation(NEXT_DAY, MORNING, dateTime(date.plusDays(1), 9)),
                new TimeRecommendation(NEXT_DAY, NOON, dateTime(date.plusDays(1), 12)),
                new TimeRecommendation(NEXT_DAY, AFTERNOON, dateTime(date.plusDays(1), 15)),
                new TimeRecommendation(NEXT_DAY, EVENING, dateTime(date.plusDays(1), 18)),
                new TimeRecommendation(ON_THE_WEEKEND, MORNING, dateTime(LocalDate.of(2021, 11, 13), 9)),
                new TimeRecommendation(NEXT_WEEK, MORNING, dateTime(LocalDate.of(2021, 11, 15), 9))
        );

        var actual = TimeHelper.getTimeRecommendations(startTime);
        assertEquals(expected, actual);
    }

    @Test
    public void getTimeRecommendations_ContainsUpcomingWeekendAndNextWeek() {
        // Monday, 2021-10-18
        var date = LocalDate.of(2021, 10, 18);
        var time = LocalTime.of(13, 39);
        var startTime = LocalDateTime.of(date, time);

        var onWeekend = new TimeRecommendation(ON_THE_WEEKEND, MORNING, dateTime(LocalDate.of(2021, 10, 23), 9));
        var nextWeek = new TimeRecommendation(NEXT_WEEK, MORNING, dateTime(LocalDate.of(2021, 10, 25), 9));

        var actual = TimeHelper.getTimeRecommendations(startTime);
        assertEquals(onWeekend, actual.get(4));
        assertEquals(nextWeek, actual.get(5));
    }

    @Test
    public void getTimeRecommendations_IsSorted() {
        // Monday, 2021-10-18
        var date = LocalDate.of(2021, 10, 18);
        var time = LocalTime.of(13, 39);
        var startTime = LocalDateTime.of(date, time);

        var actual = TimeHelper.getTimeRecommendations(startTime);
        var sorted = new ArrayList<>(actual);
        sorted.sort(Comparator.comparing(TimeRecommendation::getDateTime));
        assertEquals(sorted, actual);
    }

    private LocalDateTime dateTime(LocalDate date, int hour) {
        return LocalDateTime.of(date, LocalTime.of(hour, 0));
    }
}