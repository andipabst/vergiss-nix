package de.andicodes.vergissnix.ui.main;

import android.view.View;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import de.andicodes.vergissnix.R;

public class Converter {

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
     * Format a {@link LocalDateTime} for display. In case it is null,
     * a fallback for choosing a time is shown.
     *
     * @param view     the view that should show the time
     * @param datetime the datetime value
     * @return the formatted string
     */
    public static String dateAndTimeToString(View view, LocalDateTime datetime) {
        if (datetime == null) {
            return view.getContext().getString(R.string.custom);
        }
        return datetime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT));
    }
}
