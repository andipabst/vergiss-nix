package de.andicodes.vergissnix.ui.main

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.ResultReceiver
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import de.andicodes.vergissnix.R
import de.andicodes.vergissnix.data.Task
import de.andicodes.vergissnix.data.TimeHelper.getTimeRecommendations
import de.andicodes.vergissnix.data.TimeRecommendation.RelativeDay
import de.andicodes.vergissnix.databinding.EditTaskFragmentBinding
import de.andicodes.vergissnix.ui.dialog.LocalDatePickerDialog
import de.andicodes.vergissnix.ui.dialog.LocalDatePickerDialog.OnLocalDateSetListener
import de.andicodes.vergissnix.ui.dialog.LocalTimePickerDialog
import de.andicodes.vergissnix.ui.dialog.LocalTimePickerDialog.OnLocalTimeSetListener
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class EditTaskFragment : DialogFragment() {
    private var binding: EditTaskFragmentBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_VergissNix_FullScreenDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = EditTaskFragmentBinding.inflate(inflater, container, false)
        binding!!.lifecycleOwner = this
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = NavHostFragment.findNavController(this)
        val viewModel = ViewModelProvider(this).get(
            EditTaskViewModel::class.java
        )
        binding!!.viewModel = viewModel
        binding!!.toolBar.setTitle(R.string.add)
        binding!!.toolBar.setNavigationOnClickListener { v: View? -> dismiss() }
        binding!!.toolBar.inflateMenu(R.menu.dialog_save)
        binding!!.toolBar.setOnMenuItemClickListener { item: MenuItem ->
            if (item.itemId == R.id.action_save) {
                viewModel.saveCurrentTask(requireContext())
                navController.popBackStack()
                return@setOnMenuItemClickListener true
            }
            false
        }
        if (arguments != null) {
            val task = requireArguments().getSerializable("task") as Task?
            if (task != null) {
                viewModel.setTask(task)
                binding!!.toolBar.setTitle(R.string.edit_task)
            }
        }
        val chipGroup: ChipGroup = view.findViewById(R.id.chipGroup)
        val timeRecommendations = getTimeRecommendations(LocalDateTime.now())
        for ((relativeDay, _, dateTime) in timeRecommendations) {
            val chip = Chip(requireContext())
            var text: String? = ""
            val time = dateTime.toLocalTime()
            text = when (relativeDay) {
                RelativeDay.SAME_DAY -> getString(R.string.today) + ", " + time.format(
                    DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
                )
                RelativeDay.NEXT_DAY -> getString(R.string.tomorrow) + ", " + time.format(
                    DateTimeFormatter.ofLocalizedTime(
                        FormatStyle.SHORT
                    )
                )
                RelativeDay.ON_THE_WEEKEND -> getString(R.string.weekend)
                RelativeDay.NEXT_WEEK -> getString(R.string.next_week)
                else -> dateTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT))
            }
            chip.text = text
            chip.setOnClickListener { v: View? ->
                viewModel.setRecommendationDatetime(
                    dateTime
                )
            }
            if (dateTime == viewModel.getRecommendationDatetime().value) {
                chip.isChecked = true
            }
            chipGroup.addView(chip)
        }
        val chipCustomTime: Chip = view.findViewById(R.id.chipCustomTime)
        chipCustomTime.setOnClickListener { v: View? ->
            val oldDatetime =
                if (viewModel.getCustomDatetime().value != null) viewModel.getCustomDatetime().value else LocalDateTime.now()
            LocalDatePickerDialog(
                requireContext(),
                object : OnLocalDateSetListener {
                    override fun onDateSet(view: DatePicker?, date: LocalDate?) {
                        LocalTimePickerDialog(
                            context,
                            object : OnLocalTimeSetListener {
                                override fun onTimeSet(view: TimePicker?, time: LocalTime?) {
                                    viewModel.setCustomDatetime(
                                        LocalDateTime.of(date, time)
                                    )
                                }
                            },
                            oldDatetime!!.toLocalTime()
                        ).show()
                    }
                },
                oldDatetime!!.toLocalDate()
            ).show()
        }
        viewModel.getCustomDatetime().observe(
            viewLifecycleOwner,
            Observer { dateTime: LocalDateTime? -> chipCustomTime.isChecked = dateTime != null })
        viewModel.getRecommendationDatetime()
            .observe(viewLifecycleOwner, Observer { dateTime: LocalDateTime? ->
                // clear the selection, if no datetime is set from a recommendation
                if (dateTime == null) {
                    //chipGroup.clearCheck();
                }
            })
        val editTaskName = view.findViewById<EditText>(R.id.edit_task_name)
        editTaskName.addTextChangedListener(TimeFormatter(editTaskName))
    }

    private fun hideKeyboardFrom(context: Context, view: View) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0, object : ResultReceiver(null) {
            override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
                // clear focus on the view after the keyboard is hidden
                view.clearFocus()
            }
        })
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null && dialog.window != null) {
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    private class TimeFormatter(private val editText: EditText) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            val content = s.toString().replace("Test", "<font color='red'>Test</font>")
            val spanned = Html.fromHtml(content, Html.FROM_HTML_MODE_COMPACT)
            if (editText.text.toString() != spanned.toString()) {
                editText.setText(spanned)
            }
        }

        override fun afterTextChanged(s: Editable) {}
    }

    companion object {
        val DEFAULT_TIME: LocalTime = LocalTime.of(9, 0)
    }
}