package de.andicodes.vergissnix.ui.dialog;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;

import java.time.LocalDate;

/**
 * A time picker dialog for usage with the new java.time {@link LocalDate} class.
 */
public class LocalDatePickerDialog extends DatePickerDialog {

    /**
     * Callback interface, called when a date is set.
     */
    public interface OnLocalDateSetListener {
        /**
         * A date was selected and the dialog was closed.
         *
         * @param view the associated view
         * @param date the selected date
         */
        void onDateSet(DatePicker view, LocalDate date);
    }

    /**
     * Create a new LocalDatePickerDialog.
     *
     * @param context         the parent context
     * @param dateSetListener a listener, indicating that a date was set
     * @param initialDate     the initial date
     */
    public LocalDatePickerDialog(Context context, OnLocalDateSetListener dateSetListener, LocalDate initialDate) {
        super(
                context,
                (datePicker, year, month, dayOfMonth) -> dateSetListener.onDateSet(datePicker, LocalDate.of(year, month + 1, dayOfMonth)),
                initialDate.getYear(),
                initialDate.getMonthValue() - 1,
                initialDate.getDayOfMonth()
        );
    }
}
