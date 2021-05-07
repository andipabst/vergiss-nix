package de.andicodes.vergissnix.ui.main;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import de.andicodes.vergissnix.R;
import de.andicodes.vergissnix.data.Task;

public class TaskDialogFragment extends BottomSheetDialogFragment {

    public static final String TASK_ARGUMENT = "TaskArgument";

    @Override
    public int getTheme() {
        return R.style.BottomSheetDialogStyle;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.task_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TaskDialogViewModel viewModel = new ViewModelProvider(this).get(TaskDialogViewModel.class);
        EditText textView = view.findViewById(R.id.edit_task_name);

        if (getArguments() != null) {
            Serializable initialTask = getArguments().getSerializable(TASK_ARGUMENT);
            if (initialTask instanceof Task) {
                Task task = new Task((Task) initialTask);
                viewModel.setTask(task);
                textView.setText(task.getText());
                textView.setSelection(textView.getText().length());
            }
        }

        textView.requestFocus();
        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setText(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        MaterialButton dateButton = view.findViewById(R.id.date);
        dateButton.setOnClickListener(v -> {
            LocalDate oldDate = viewModel.getDueDate().getValue();
            if (oldDate == null) {
                oldDate = LocalDate.now();
            }
            new DatePickerDialog(
                    getContext(),
                    (datePicker, year, month, dayOfMonth) -> viewModel.setDate(year, month + 1, dayOfMonth),
                    oldDate.getYear(),
                    oldDate.getMonthValue() - 1,
                    oldDate.getDayOfMonth()
            ).show();
        });

        MaterialButton timeButton = view.findViewById(R.id.time);
        timeButton.setOnClickListener(v -> {
            LocalTime oldTime = viewModel.getDueTime().getValue();
            if (oldTime == null) {
                oldTime = LocalTime.now();
            }
            new TimePickerDialog(
                    getContext(),
                    (timePicker, hourOfDay, minute) -> viewModel.setTime(hourOfDay, minute),
                    oldTime.getHour(),
                    oldTime.getMinute(),
                    DateFormat.is24HourFormat(getContext())
            ).show();
        });

        MaterialButton saveButton = view.findViewById(R.id.save);
        saveButton.setOnClickListener(v -> {
            viewModel.saveCurrentTask(getContext());
            NavHostFragment.findNavController(this).popBackStack();
        });

        viewModel.getTaskLiveData().observe(getViewLifecycleOwner(), newTask -> {
            if (textView.getText().toString().equals(newTask.getText())) {
                //
            }

            if (newTask.getTime() != null) {
                dateButton.setText(newTask.getTime().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)));
                timeButton.setText(newTask.getTime().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)));
            } else {
                dateButton.setText("Datum wählen");
                timeButton.setText("Zeit wählen");
            }
        });
    }
}
