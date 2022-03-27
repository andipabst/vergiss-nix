package de.andicodes.vergissnix.ui.dialog;

import android.app.TimePickerDialog;
import android.content.Context;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.time.LocalTime;

/**
 * A time picker dialog for usage with the new java.time {@link LocalTime} class.
 */
public class LocalTimePickerDialog extends TimePickerDialog {

    /**
     * Callback interface, called when a time is set.
     */
    public interface OnLocalTimeSetListener {
        /**
         * A time was selected and the dialog was closed.
         *
         * @param view the associated view
         * @param time the selected time
         */
        void onTimeSet(TimePicker view, LocalTime time);
    }

    /**
     * Create a new LocalTimePickerDialog.
     *
     * @param context         the parent context
     * @param timeSetListener a listener, indicating that a time was set
     * @param initialTime     the initial time
     */
    public LocalTimePickerDialog(Context context, OnLocalTimeSetListener timeSetListener, LocalTime initialTime) {
        super(
                context,
                (timePicker, hourOfDay, minute) -> timeSetListener.onTimeSet(timePicker, LocalTime.of(hourOfDay, minute)),
                initialTime.getHour(),
                initialTime.getMinute(),
                DateFormat.is24HourFormat(context)
        );
    }
}
