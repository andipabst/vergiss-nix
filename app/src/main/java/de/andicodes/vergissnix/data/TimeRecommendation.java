package de.andicodes.vergissnix.data;

import java.time.LocalDateTime;
import java.util.Objects;

public class TimeRecommendation {

    public enum RelativeDay {
        SAME_DAY, NEXT_DAY, ON_THE_WEEKEND, NEXT_WEEK
    }

    public enum RelativeTime {
        MORNING, NOON, AFTERNOON, EVENING
    }

    private final RelativeDay relativeDay;
    private final RelativeTime relativeTime;
    private final LocalDateTime dateTime;

    public TimeRecommendation(RelativeDay relativeDay, RelativeTime relativeTime, LocalDateTime dateTime) {
        this.relativeDay = relativeDay;
        this.relativeTime = relativeTime;
        this.dateTime = dateTime;
    }

    public RelativeDay getRelativeDay() {
        return relativeDay;
    }

    public RelativeTime getRelativeTime() {
        return relativeTime;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    @Override
    public String toString() {
        return "TimeRecommendation{" +
                "relativeDay=" + relativeDay +
                ", relativeTime=" + relativeTime +
                ", dateTime=" + dateTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeRecommendation that = (TimeRecommendation) o;
        return relativeDay == that.relativeDay && relativeTime == that.relativeTime && Objects.equals(dateTime, that.dateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(relativeDay, relativeTime, dateTime);
    }
}
