package de.andicodes.vergissnix.ui.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.andicodes.vergissnix.R;
import de.andicodes.vergissnix.data.Task;

public class TaskListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private enum TemporalGrouping {
        OVERDUE, TODAY, TOMORROW, THIS_WEEK, THIS_MONTH, LATER
    }

    private final ArrayList<ListItem> tasks = new ArrayList<>();
    private final TaskListener taskListener;

    public TaskListAdapter(Consumer<Task> editTask) {
        this.taskListener = editTask::accept;
    }

    public void replaceTasks(List<Task> tasks) {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime today = now.withHour(0).withMinute(0).withSecond(0).withNano(0);

        var tasksWithHeadings = tasks.stream()
                .collect(Collectors.groupingBy(task -> {
                    if (task.getTime() == null || task.getTime().isBefore(today)) {
                        return TemporalGrouping.OVERDUE;
                    } else if (task.getTime().isAfter(today) && task.getTime().isBefore(today.plusDays(1))) {
                        return TemporalGrouping.TODAY;
                    } else if (task.getTime().isAfter(today.plusDays(1)) && task.getTime().isBefore(today.plusDays(2))) {
                        return TemporalGrouping.TOMORROW;
                    } else if (task.getTime().isAfter(today.plusDays(2)) && task.getTime().isBefore(today.plusWeeks(1))) {
                        return TemporalGrouping.THIS_WEEK;
                    } else if (task.getTime().isAfter(today.plusWeeks(1)) && task.getTime().isBefore(today.plusMonths(1))) {
                        return TemporalGrouping.THIS_MONTH;
                    } else {
                        return TemporalGrouping.LATER;
                    }
                }))
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .flatMap(entry -> Stream.concat(
                        Stream.of(new HeaderItem(entry.getKey())),
                        entry.getValue().stream().map(TaskItem::new)))
                .collect(Collectors.toList());

        this.tasks.clear();
        this.tasks.addAll(tasksWithHeadings);
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ListItem.HEADER_TYPE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_list_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_list_item, parent, false);
            return new TaskViewHolder(view, taskListener);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == ListItem.HEADER_TYPE) {
            HeaderItem headerItem = (HeaderItem) tasks.get(position);
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            headerViewHolder.setTemporalGrouping(headerItem.getTemporalGrouping());
        } else if (viewType == ListItem.TASK_TYPE) {
            TaskItem taskItem = (TaskItem) tasks.get(position);
            TaskViewHolder taskViewHolder = (TaskViewHolder) holder;
            taskViewHolder.setTask(taskItem.getTask());
        }
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    @Override
    public int getItemViewType(int position) {
        return tasks.get(position).getType();
    }

    public Task getTask(int adapterPosition) {
        ListItem listItem = tasks.get(adapterPosition);
        if (listItem instanceof TaskItem) {
            return ((TaskItem) listItem).getTask();
        }
        return null;
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {

        final View foregroundView;
        private final TextView taskName;
        private final TextView dueDateTime;
        private Task task;

        public TaskViewHolder(@NonNull View itemView, TaskListener taskListener) {
            super(itemView);
            taskName = itemView.findViewById(R.id.task_name);
            dueDateTime = itemView.findViewById(R.id.due_date_time);
            foregroundView = itemView.findViewById(R.id.task_item_foreground);
            itemView.setOnClickListener(v -> taskListener.editTask(task));
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

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {

        private final TextView taskName;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            taskName = itemView.findViewById(R.id.text);
        }

        public void setTemporalGrouping(TemporalGrouping temporalGrouping) {
            switch (temporalGrouping) {
                case OVERDUE:
                    taskName.setText(R.string.overdue);
                    break;
                case TODAY:
                    taskName.setText(R.string.today);
                    break;
                case TOMORROW:
                    taskName.setText(R.string.tomorrow);
                    break;
                case THIS_WEEK:
                    taskName.setText(R.string.this_week);
                    break;
                case THIS_MONTH:
                    taskName.setText(R.string.this_month);
                    break;
                case LATER:
                    taskName.setText(R.string.later);
                    break;
            }
        }
    }

    private interface TaskListener {
        void editTask(Task task);
    }

    static abstract class ListItem {

        public static final int HEADER_TYPE = 1;
        public static final int TASK_TYPE = 2;

        abstract public int getType();
    }

    static class HeaderItem extends ListItem {

        private final TemporalGrouping temporalGrouping;

        public HeaderItem(TemporalGrouping temporalGrouping) {
            this.temporalGrouping = temporalGrouping;
        }

        @Override
        public int getType() {
            return HEADER_TYPE;
        }

        public TemporalGrouping getTemporalGrouping() {
            return temporalGrouping;
        }
    }

    static class TaskItem extends ListItem {

        private final Task task;

        public TaskItem(Task task) {
            this.task = task;
        }

        @Override
        public int getType() {
            return TASK_TYPE;
        }

        public Task getTask() {
            return task;
        }
    }
}
