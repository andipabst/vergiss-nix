package de.andicodes.vergissnix.ui.main;

import android.view.View;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import de.andicodes.vergissnix.R;

public class Converter {

    /**
     * Format a {@link LocalDate} for display. In case it is null, a fallback
     * for choosing a date is shown.
     *
     * @param view the view that should show the date
     * @param date the date value
     * @return the formatted date string
     */
    public static String dateToString(View view, LocalDate date) {
        if (date == null) {
            return view.getContext().getString(R.string.chooseDate);
        }
        return date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT));
    }


    /**
     * Format a {@link LocalTime} for display. In case it is null,
     * a fallback for choosing a time is shown.
     *
     * @param view the view that should show the time
     * @param time the time value
     * @return the formatted string
     */
    public static String timeToString(View view, LocalTime time) {
        if (time == null) {
            return view.getContext().getString(R.string.chooseTime);
        }
        return time.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT));
    }

    /**
     * Format a {@link LocalDate} and {@link LocalTime} for display. In case it is null,
     * a fallback for choosing a time is shown.
     *
     * @param view the view that should show the time
     * @param date the date value
     * @param time the time value
     * @return the formatted string
     */
    public static String dateAndTimeToString(View view, LocalDate date, LocalTime time) {
        if (time == null || date == null) {
            return view.getContext().getString(R.string.custom);
        }
        return LocalDateTime.of(date, time).format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT));
    }
}
