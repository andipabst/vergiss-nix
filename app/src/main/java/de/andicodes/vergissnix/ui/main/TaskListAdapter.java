package de.andicodes.vergissnix.ui.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.andicodes.vergissnix.R;
import de.andicodes.vergissnix.data.Task;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.ViewHolder> {

    private final List<Task> tasks = new ArrayList<>();
    private final Consumer<Task> editTask;

    public TaskListAdapter(Consumer<Task> editTask) {
        this.editTask = editTask;
    }

    public void replaceTasks(List<Task> tasks) {
        this.tasks.clear();
        this.tasks.addAll(tasks);
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_list_item, parent, false);
        return new ViewHolder(view, editTask);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setTask(tasks.get(position));
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView taskName;
        private final TextView dueDateTime;
        private Task task;

        public ViewHolder(@NonNull View itemView, Consumer<Task> editTask) {
            super(itemView);
            taskName = itemView.findViewById(R.id.task_name);
            dueDateTime = itemView.findViewById(R.id.due_date_time);
            itemView.setOnClickListener(v -> editTask.accept(task));
        }

        public void setTask(Task task) {
            this.task = task;
            taskName.setText(task.getText());
            if (task.getTime() != null) {
                dueDateTime.setVisibility(View.VISIBLE);
                dueDateTime.setText(task.getTime().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)));
            } else {
                dueDateTime.setVisibility(View.GONE);
            }
        }
    }
}
