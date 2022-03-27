package de.andicodes.vergissnix.ui.dialog

import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.text.format.DateFormat
import android.widget.TimePicker
import java.time.LocalTime

/**
 * A time picker dialog for usage with the new java.time [LocalTime] class.
 */
class LocalTimePickerDialog
/**
 * Create a new LocalTimePickerDialog.
 *
 * @param context         the parent context
 * @param timeSetListener a listener, indicating that a time was set
 * @param initialTime     the initial time
 */
    (context: Context?, timeSetListener: OnLocalTimeSetListener, initialTime: LocalTime) :
    TimePickerDialog(
        context,
        OnTimeSetListener { timePicker: TimePicker?, hourOfDay: Int, minute: Int ->
            timeSetListener.onTimeSet(
                timePicker,
                LocalTime.of(hourOfDay, minute)
            )
        },
        initialTime.hour,
        initialTime.minute,
        DateFormat.is24HourFormat(context)
    ) {
    /**
     * Callback interface, called when a time is set.
     */
    interface OnLocalTimeSetListener {
        /**
         * A time was selected and the dialog was closed.
         *
         * @param view the associated view
         * @param time the selected time
         */
        fun onTimeSet(view: TimePicker?, time: LocalTime?)
    }
}