package de.andicodes.vergissnix.ui.main;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import de.andicodes.vergissnix.R;
import de.andicodes.vergissnix.data.Task;
import de.andicodes.vergissnix.data.TimeHelper;
import de.andicodes.vergissnix.data.TimeRecommendation;
import de.andicodes.vergissnix.databinding.EditTaskFragmentBinding;
import de.andicodes.vergissnix.ui.dialog.LocalDatePickerDialog;
import de.andicodes.vergissnix.ui.dialog.LocalTimePickerDialog;

public class EditTaskFragment extends DialogFragment {

    private EditTaskFragmentBinding binding;
    public static final LocalTime DEFAULT_TIME = LocalTime.of(9, 0);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_VergissNix_FullScreenDialog);
    }

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
        NavController navController = NavHostFragment.findNavController(this);
        EditTaskViewModel viewModel = new ViewModelProvider(this).get(EditTaskViewModel.class);
        binding.setViewModel(viewModel);

        binding.toolBar.setTitle(R.string.add);
        binding.toolBar.setNavigationOnClickListener(v -> dismiss());
        binding.toolBar.inflateMenu(R.menu.dialog_save);
        binding.toolBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_save) {
                viewModel.saveCurrentTask(requireContext());
                navController.popBackStack();
                return true;
            }
            return false;
        });

        if (getArguments() != null) {
            Task task = (Task) getArguments().getSerializable("task");
            if (task != null) {
                viewModel.setTask(task);
                binding.toolBar.setTitle(R.string.edit_task);
            }
        }

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
            if (timeRecommendation.getDateTime().equals(viewModel.getRecommendationDatetime().getValue())) {
                chip.setChecked(true);
            }
            chipGroup.addView(chip);
        }

        Chip chipCustomTime = view.findViewById(R.id.chipCustomTime);
        chipCustomTime.setOnClickListener(v -> {
            final LocalDateTime oldDatetime = viewModel.getCustomDatetime().getValue() != null
                    ? viewModel.getCustomDatetime().getValue()
                    : LocalDateTime.now();

            new LocalDatePickerDialog(requireContext(), (datePicker, date) -> {
                new LocalTimePickerDialog(getContext(), (timePicker, time) -> {
                    viewModel.setCustomDatetime(LocalDateTime.of(date, time));
                }, oldDatetime.toLocalTime()).show();
            }, oldDatetime.toLocalDate()).show();
        });

        viewModel.getCustomDatetime().observe(getViewLifecycleOwner(), dateTime -> chipCustomTime.setChecked(dateTime != null));

        viewModel.getRecommendationDatetime().observe(getViewLifecycleOwner(), dateTime -> {
            // clear the selection, if no datetime is set from a recommendation

            if (dateTime == null) {
                //chipGroup.clearCheck();
            }
        });

        EditText editTaskName = view.findViewById(R.id.edit_task_name);
        editTaskName.addTextChangedListener(new TimeFormatter(editTaskName));
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

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    private static class TimeFormatter implements TextWatcher {

        private final EditText editText;

        public TimeFormatter(EditText editText) {
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String content = s.toString().replace("Test", "<font color='red'>Test</font>");
            Spanned spanned = Html.fromHtml(content, Html.FROM_HTML_MODE_COMPACT);


            if (!editText.getText().toString().equals(spanned.toString())) {
                editText.setText(spanned);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}
