package de.andicodes.vergissnix.data;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

import static de.andicodes.vergissnix.data.TimeRecommendation.RelativeDay.NEXT_DAY;
import static de.andicodes.vergissnix.data.TimeRecommendation.RelativeDay.NEXT_WEEK;
import static de.andicodes.vergissnix.data.TimeRecommendation.RelativeDay.ON_THE_WEEKEND;
import static de.andicodes.vergissnix.data.TimeRecommendation.RelativeDay.SAME_DAY;
import static de.andicodes.vergissnix.data.TimeRecommendation.RelativeTime.AFTERNOON;
import static de.andicodes.vergissnix.data.TimeRecommendation.RelativeTime.EVENING;
import static de.andicodes.vergissnix.data.TimeRecommendation.RelativeTime.MORNING;
import static de.andicodes.vergissnix.data.TimeRecommendation.RelativeTime.NOON;

public class TimeHelper {

    private static final int MORNING_HOUR = 9;
    private static final int NOON_HOUR = 12;
    private static final int AFTERNOON_HOUR = 15;
    private static final int EVENING_HOUR = 18;

    public static List<TimeRecommendation> getTimeRecommendations(LocalDateTime currentTime) {
        ArrayList<TimeRecommendation> timeRecommendationsSameDay = new ArrayList<>();
        ArrayList<TimeRecommendation> timeRecommendationsNextDay = new ArrayList<>();

        currentTime = currentTime.withMinute(0).withSecond(0).withNano(0);

        final LocalDate date = currentTime.toLocalDate();
        final LocalTime time = currentTime.toLocalTime();

        // add recommendations for the next 24 hours
        if (time.isBefore(LocalTime.of(MORNING_HOUR, 0))) {
            timeRecommendationsSameDay.add(new TimeRecommendation(SAME_DAY, MORNING, currentTime.withHour(MORNING_HOUR)));
        } else {
            timeRecommendationsNextDay.add(new TimeRecommendation(NEXT_DAY, MORNING, currentTime.plusDays(1).withHour(MORNING_HOUR)));
        }

        if (time.isBefore(LocalTime.of(NOON_HOUR, 0))) {
            timeRecommendationsSameDay.add(new TimeRecommendation(SAME_DAY, NOON, currentTime.withHour(NOON_HOUR)));
        } else {
            timeRecommendationsNextDay.add(new TimeRecommendation(NEXT_DAY, NOON, currentTime.plusDays(1).withHour(NOON_HOUR)));
        }

        if (time.isBefore(LocalTime.of(AFTERNOON_HOUR, 0))) {
            timeRecommendationsSameDay.add(new TimeRecommendation(SAME_DAY, AFTERNOON, currentTime.withHour(AFTERNOON_HOUR)));
        } else {
            timeRecommendationsNextDay.add(new TimeRecommendation(NEXT_DAY, AFTERNOON, currentTime.plusDays(1).withHour(AFTERNOON_HOUR)));
        }

        if (time.isBefore(LocalTime.of(EVENING_HOUR, 0))) {
            timeRecommendationsSameDay.add(new TimeRecommendation(SAME_DAY, EVENING, currentTime.withHour(EVENING_HOUR)));
        } else {
            timeRecommendationsNextDay.add(new TimeRecommendation(NEXT_DAY, EVENING, currentTime.plusDays(1).withHour(EVENING_HOUR)));
        }

        ArrayList<TimeRecommendation> result = new ArrayList<>();
        result.addAll(timeRecommendationsSameDay);
        result.addAll(timeRecommendationsNextDay);

        result.add(new TimeRecommendation(ON_THE_WEEKEND, MORNING, LocalDateTime.of(date.with(TemporalAdjusters.next(DayOfWeek.SATURDAY)), LocalTime.of(MORNING_HOUR, 0))));
        result.add(new TimeRecommendation(NEXT_WEEK, MORNING, LocalDateTime.of(date.with(TemporalAdjusters.next(DayOfWeek.MONDAY)), LocalTime.of(MORNING_HOUR, 0))));

        return result;
    }

}
