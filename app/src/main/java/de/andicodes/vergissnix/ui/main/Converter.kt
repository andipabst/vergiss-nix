package de.andicodes.vergissnix.ui.main

import android.view.View
import de.andicodes.vergissnix.R
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

object Converter {
    /**
     * Format a [LocalTime] for display. In case it is null,
     * a fallback for choosing a time is shown.
     *
     * @param view the view that should show the time
     * @param time the time value
     * @return the formatted string
     */
    fun timeToString(view: View, time: LocalTime?): String {
        return if (time == null) {
            view.context.getString(R.string.chooseTime)
        } else time.format(
            DateTimeFormatter.ofLocalizedTime(
                FormatStyle.SHORT
            )
        )
    }

    /**
     * Format a [LocalDateTime] for display. In case it is null,
     * a fallback for choosing a time is shown.
     *
     * @param view     the view that should show the time
     * @param datetime the datetime value
     * @return the formatted string
     */
    @JvmStatic
    fun dateAndTimeToString(view: View, datetime: LocalDateTime?): String {
        return if (datetime == null) {
            view.context.getString(R.string.custom)
        } else datetime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT))
    }
}