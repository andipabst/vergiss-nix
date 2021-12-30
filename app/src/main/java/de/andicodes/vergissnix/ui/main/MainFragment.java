package de.andicodes.vergissnix.ui.main;

import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.andicodes.vergissnix.NotificationBroadcastReceiver;
import de.andicodes.vergissnix.R;
import de.andicodes.vergissnix.data.Task;

public class MainFragment extends Fragment {

    private MainViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        NavController navController = NavHostFragment.findNavController(this);

        RecyclerView taskList = view.findViewById(R.id.task_list);
        taskList.setLayoutManager(new LinearLayoutManager(requireContext()));
        TaskListAdapter taskListAdapter = new TaskListAdapter(task -> {
            if (task != null) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("task", task);
                navController.navigate(R.id.action_mainFragment_to_editTaskFragment, bundle);
            }
        });
        taskList.setAdapter(taskListAdapter);
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                // no drag and drop
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final Task task = taskListAdapter.getTask(viewHolder.getAdapterPosition());
                viewModel.markTaskDone(task);
                Snackbar.make(requireView(), R.string.taskDone, Snackbar.LENGTH_LONG)
                        .setAction(R.string.undo, v -> viewModel.markTaskNotDone(task))
                        .show();
                NotificationBroadcastReceiver.cancelNotification(requireContext(), task.getId());
            }

            @Override
            public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                // just move the foreground view instead of the whole element -> background view is exposed
                final View foregroundView = ((TaskListAdapter.TaskViewHolder) viewHolder).foregroundView;
                getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                // just move the foreground view instead of the whole element -> background view is exposed
                final View foregroundView = ((TaskListAdapter.TaskViewHolder) viewHolder).foregroundView;
                getDefaultUIUtil().clearView(foregroundView);
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                // just move the foreground view instead of the whole element -> background view is exposed
                final View foregroundView = ((TaskListAdapter.TaskViewHolder) viewHolder).foregroundView;
                getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
            }
        };
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(taskList);
        viewModel.currentTasks().observe(getViewLifecycleOwner(), taskListAdapter::replaceTasks);

        ExtendedFloatingActionButton addTaskButton = view.findViewById(R.id.add_task);
        addTaskButton.setOnClickListener(v -> {
            navController.navigate(R.id.action_mainFragment_to_editTaskFragment);
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private int getActiveFilterPosition() {
        MainViewModel.TaskFilter filterValue = viewModel.getFilter();
        if (filterValue == null) {
            return 1;
        } else {
            return filterValue.getPosition();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.filter_button) {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.filter)
                    .setSingleChoiceItems(R.array.filter_items, getActiveFilterPosition(),
                            (dialog, filterPosition) -> viewModel.setFilter(MainViewModel.TaskFilter.of(filterPosition)))
                    .show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}