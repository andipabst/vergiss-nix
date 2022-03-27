package de.andicodes.vergissnix.ui.dialog

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.widget.DatePicker
import java.time.LocalDate

/**
 * A time picker dialog for usage with the new java.time [LocalDate] class.
 */
class LocalDatePickerDialog
/**
 * Create a new LocalDatePickerDialog.
 *
 * @param context         the parent context
 * @param dateSetListener a listener, indicating that a date was set
 * @param initialDate     the initial date
 */
    (context: Context, dateSetListener: OnLocalDateSetListener, initialDate: LocalDate) :
    DatePickerDialog(
        context,
        OnDateSetListener { datePicker: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
            dateSetListener.onDateSet(
                datePicker,
                LocalDate.of(year, month + 1, dayOfMonth)
            )
        },
        initialDate.year,
        initialDate.monthValue - 1,
        initialDate.dayOfMonth
    ) {
    /**
     * Callback interface, called when a date is set.
     */
    interface OnLocalDateSetListener {
        /**
         * A date was selected and the dialog was closed.
         *
         * @param view the associated view
         * @param date the selected date
         */
        fun onDateSet(view: DatePicker?, date: LocalDate?)
    }
}