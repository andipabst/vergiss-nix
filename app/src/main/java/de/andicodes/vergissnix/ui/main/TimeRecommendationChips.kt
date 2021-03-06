package de.andicodes.vergissnix.ui.main

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.DatePicker
import android.widget.TimePicker
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import de.andicodes.vergissnix.R
import de.andicodes.vergissnix.data.TimeRecommendation
import de.andicodes.vergissnix.ui.dialog.LocalDatePickerDialog
import de.andicodes.vergissnix.ui.dialog.LocalTimePickerDialog
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class TimeRecommendationChips(context: Context) : ChipGroup(context) {

    var timeRecommendations: List<TimeRecommendation> = listOf()
        set(value) {
            field = value
            for ((relativeDay, _, dateTime) in timeRecommendations) {
                val chip = Chip(context)
                val time = dateTime.toLocalTime()
                chip.text = when (relativeDay) {
                    TimeRecommendation.RelativeDay.SAME_DAY -> context.getString(R.string.today) + ", " + time.format(
                        DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
                    )
                    TimeRecommendation.RelativeDay.NEXT_DAY -> context.getString(R.string.tomorrow) + ", " + time.format(
                        DateTimeFormatter.ofLocalizedTime(
                            FormatStyle.SHORT
                        )
                    )
                    TimeRecommendation.RelativeDay.ON_THE_WEEKEND -> context.getString(R.string.weekend)
                    TimeRecommendation.RelativeDay.NEXT_WEEK -> context.getString(R.string.next_week)
                }
                chip.setOnClickListener {
                    selectionRecommendationChangedListener(dateTime)
                }
                chip.background =
                    if (dateTime == selectedCustom) ColorDrawable(0xffFFA000.toInt()) else ColorDrawable(
                        0xffdddddd.toInt()
                    )
                chip.isChecked = dateTime == selectedCustom
                addView(chip)
            }

        }

    var selectedRecommendation: LocalDateTime? = null
    var selectionRecommendationChangedListener: (LocalDateTime) -> Unit = {}

    var selectedCustom: LocalDateTime? = null
    var selectionCustomChangedListener: (LocalDateTime) -> Unit = {}

    init {
        isSingleSelection = true

        val chipCustomTime = Chip(context)
        chipCustomTime.text = Converter.dateAndTimeToString(chipCustomTime, selectedCustom)
        chipCustomTime.setOnClickListener {
            val oldDatetime = selectedCustom ?: LocalDateTime.now()
            LocalDatePickerDialog(
                context,
                object : LocalDatePickerDialog.OnLocalDateSetListener {
                    override fun onDateSet(view: DatePicker?, date: LocalDate?) {
                        LocalTimePickerDialog(
                            context,
                            object : LocalTimePickerDialog.OnLocalTimeSetListener {
                                override fun onTimeSet(view: TimePicker?, time: LocalTime?) {
                                    selectionCustomChangedListener(
                                        LocalDateTime.of(
                                            date,
                                            time
                                        )
                                    )
                                }
                            },
                            oldDatetime.toLocalTime()
                        ).show()
                    }
                },
                oldDatetime.toLocalDate()
            ).show()
        }
        addView(chipCustomTime)

    }

}