package de.andicodes.vergissnix.ui.main;

import android.app.Application;
import android.content.Context;

import java.time.ZonedDateTime;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import de.andicodes.vergissnix.NotificationBroadcastReceiver;
import de.andicodes.vergissnix.data.AppDatabase;
import de.andicodes.vergissnix.data.Task;
import de.andicodes.vergissnix.data.TaskDao;

public class MainViewModel extends AndroidViewModel {
    private final MutableLiveData<TaskFilter> filter = new MutableLiveData<>(TaskFilter.COMING_WEEK);
    private final TaskDao taskDao;

    public enum TaskFilter {
        DONE(0), COMING_WEEK(1), COMING_MONTH(2), COMING_ALL(3);

        private final int position;

        TaskFilter(int position) {
            this.position = position;
        }

        public int getPosition() {
            return position;
        }

        public static TaskFilter of(int position) {
            for (TaskFilter filter : TaskFilter.values()) {
                if (filter.position == position) {
                    return filter;
                }
            }
            return COMING_WEEK;
        }
    }

    public MainViewModel(@NonNull Application application) {
        super(application);
        this.taskDao = AppDatabase.getDatabase(application).taskDao();
    }

    @VisibleForTesting
    MainViewModel(Application application, TaskDao taskDao) {
        super(application);
        this.taskDao = taskDao;
    }

    public LiveData<List<Task>> currentTasks() {
        return Transformations.switchMap(filter, filterValue -> {
            if (filterValue == null) {
                return taskDao.allTasks(ZonedDateTime.now().plusWeeks(1));
            }

            switch (filterValue) {
                case DONE:
                    return taskDao.doneTasks();
                case COMING_WEEK:
                    return taskDao.allTasks(ZonedDateTime.now().plusWeeks(1));
                case COMING_MONTH:
                    return taskDao.allTasks(ZonedDateTime.now().plusMonths(1));
                case COMING_ALL:
                    return taskDao.allTasks();
                default:
                    return taskDao.allTasks(ZonedDateTime.now().plusWeeks(1));
            }
        });
    }

    public void markTaskDone(Task task) {
        task.setTimeDone(ZonedDateTime.now());
        AppDatabase.databaseWriteExecutor.execute(() -> taskDao.saveTask(task));
    }

    public void markTaskNotDone(Task task) {
        task.setTimeDone(null);
        AppDatabase.databaseWriteExecutor.execute(() -> taskDao.saveTask(task));
    }

    public TaskFilter getFilter() {
        return filter.getValue();
    }

    public void setFilter(TaskFilter filter) {
        this.filter.setValue(filter);
    }
}