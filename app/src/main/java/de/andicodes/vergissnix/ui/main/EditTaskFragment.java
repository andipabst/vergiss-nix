package de.andicodes.vergissnix.ui.main;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

import java.time.LocalDate;
import java.time.LocalTime;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import de.andicodes.vergissnix.R;
import de.andicodes.vergissnix.databinding.EditTaskFragmentBinding;
import de.andicodes.vergissnix.ui.dialog.LocalDatePickerDialog;
import de.andicodes.vergissnix.ui.dialog.LocalTimePickerDialog;

public class EditTaskFragment extends Fragment {

    private EditTaskFragmentBinding binding;
    public static final LocalTime DEFAULT_TIME = LocalTime.of(9, 0);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = EditTaskFragmentBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainViewModel mainViewModel = new ViewModelProvider(requireParentFragment()).get(MainViewModel.class);
        EditTaskViewModel viewModel = new ViewModelProvider(this).get(EditTaskViewModel.class);
        binding.setViewModel(viewModel);
        mainViewModel.getEditedTaskLiveData().observe(getViewLifecycleOwner(), task -> {
            viewModel.setTask(task);
            if (task == null) {
                hideKeyboardFrom(requireContext(), binding.editTaskName);
            }
        });

        Chip chip4 = view.findViewById(R.id.chip4);
        chip4.setOnClickListener(v -> {
            LocalDate oldDate = viewModel.getDate().getValue();
            if (oldDate == null) {
                oldDate = LocalDate.now();
            }
            final LocalTime oldTime = viewModel.getTime().getValue() != null ? viewModel.getTime().getValue() : DEFAULT_TIME;

            new LocalDatePickerDialog(getContext(), (datePicker, date) -> {
                viewModel.setDate(date);
                new LocalTimePickerDialog(getContext(), (timePicker, time) -> viewModel.setTime(time), oldTime).show();
            }, oldDate).show();
        });

        AppCompatImageButton saveButton = view.findViewById(R.id.save);
        saveButton.setOnClickListener(v -> mainViewModel.saveTask(getContext(), viewModel.getTask()));
    }

    private void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0, new ResultReceiver(null) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                // clear focus on the view after the keyboard is hidden
                view.clearFocus();
            }
        });
    }

}
