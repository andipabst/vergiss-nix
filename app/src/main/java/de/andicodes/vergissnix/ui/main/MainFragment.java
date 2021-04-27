package de.andicodes.vergissnix.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MainViewModel viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        FloatingActionButton createTaskButton = requireView().findViewById(R.id.create_task);
        createTaskButton.setOnClickListener(v -> NavHostFragment
                .findNavController(this)
                .navigate(R.id.action_mainFragment_to_taskDialogFragment)
        );

        RecyclerView taskList = requireView().findViewById(R.id.task_list);
        taskList.setLayoutManager(new LinearLayoutManager(requireContext()));
        TaskListAdapter taskListAdapter = new TaskListAdapter();
        taskList.setAdapter(taskListAdapter);
        viewModel.currentTasks().observe(getViewLifecycleOwner(), taskListAdapter::replaceTasks);
    }
}