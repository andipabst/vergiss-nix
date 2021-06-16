package de.andicodes.vergissnix.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.andicodes.vergissnix.R;

public class MainFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainViewModel viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        View dimBackground = view.findViewById(R.id.dim_background);

        RecyclerView taskList = view.findViewById(R.id.task_list);
        taskList.setLayoutManager(new LinearLayoutManager(requireContext()));
        TaskListAdapter taskListAdapter = new TaskListAdapter(editedTask -> {
            viewModel.setEditedTask(editedTask);
            dimBackground.setVisibility(View.VISIBLE);
        }, viewModel::deleteTask);
        taskList.setAdapter(taskListAdapter);
        viewModel.currentTasks().observe(getViewLifecycleOwner(), taskListAdapter::replaceTasks);

        FragmentContainerView bottomSheet = view.findViewById(R.id.bottom_sheet);
        BottomSheetBehavior<FragmentContainerView> bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        viewModel.getEditedTaskLiveData().observe(getViewLifecycleOwner(), task -> {
            if (task == null) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        dimBackground.setOnClickListener(v -> {
            viewModel.setEditedTask(null);
            dimBackground.setVisibility(View.GONE);
        });
    }
}