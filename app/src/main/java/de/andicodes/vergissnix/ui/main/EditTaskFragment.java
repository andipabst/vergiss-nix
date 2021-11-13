package de.andicodes.vergissnix.ui.main;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import de.andicodes.vergissnix.R;
import de.andicodes.vergissnix.data.TimeHelper;
import de.andicodes.vergissnix.data.TimeRecommendation;
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

        ChipGroup chipGroup = view.findViewById(R.id.chipGroup);
        List<TimeRecommendation> timeRecommendations = TimeHelper.getTimeRecommendations(LocalDateTime.now());
        for (var timeRecommendation : timeRecommendations) {
            Chip chip = new Chip(requireContext());
            String text = "";
            LocalTime time = timeRecommendation.getDateTime().toLocalTime();

            switch (timeRecommendation.getRelativeDay()) {
                case SAME_DAY:
                    text = getString(R.string.today) + ", " + time.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT));
                    break;
                case NEXT_DAY:
                    text = getString(R.string.tomorrow) + ", " + time.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT));
                    break;
                case ON_THE_WEEKEND:
                    text = getString(R.string.weekend);
                    break;
                case NEXT_WEEK:
                    text = getString(R.string.next_week);
                    break;
                default:
                    text = timeRecommendation.getDateTime().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT));
                    break;
            }

            chip.setText(text);
            chip.setOnClickListener(v -> viewModel.setRecommendationDatetime(timeRecommendation.getDateTime()));
            chipGroup.addView(chip);
        }

        Chip chipCustomTime = view.findViewById(R.id.chipCustomTime);
        chipCustomTime.setOnClickListener(v -> {
            final LocalDateTime oldDatetime = viewModel.getCustomDatetime().getValue() != null
                    ? viewModel.getCustomDatetime().getValue()
                    : LocalDateTime.now();

            new LocalDatePickerDialog(getContext(), (datePicker, date) -> {
                new LocalTimePickerDialog(getContext(), (timePicker, time) -> {
                    viewModel.setCustomDatetime(LocalDateTime.of(date, time));
                }, oldDatetime.toLocalTime()).show();
            }, oldDatetime.toLocalDate()).show();
        });

        AppCompatImageButton saveButton = view.findViewById(R.id.save);
        saveButton.setOnClickListener(v -> mainViewModel.saveTask(getContext(), viewModel.getTask()));

        viewModel.getCustomDatetime().observe(getViewLifecycleOwner(), dateTime -> chipCustomTime.setChecked(dateTime != null));

        viewModel.getRecommendationDatetime().observe(getViewLifecycleOwner(), dateTime -> {
            // clear the selection, if no datetime is set from a recommendation
            if (dateTime == null) {
                chipGroup.clearCheck();
            }
        });
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
