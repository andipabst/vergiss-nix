package de.andicodes.vergissnix.ui.main;

import android.app.Application;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import de.andicodes.vergissnix.data.AppDatabase;
import de.andicodes.vergissnix.data.Task;
import de.andicodes.vergissnix.data.TaskDao;

public class TaskDialogViewModel extends AndroidViewModel {
    private final MutableLiveData<Task> taskLiveData = new MutableLiveData<>(new Task());
    private final LiveData<LocalTime> dueTime = Transformations.map(taskLiveData,
            task -> task.getTime() != null ? task.getTime().toLocalTime() : LocalTime.now());
    private final LiveData<LocalDate> dueDate = Transformations.map(taskLiveData,
            task -> task.getTime() != null ? task.getTime().toLocalDate() : LocalDate.now());

    private final TaskDao taskDao;

    public TaskDialogViewModel(@NonNull Application application) {
        super(application);
        this.taskDao = AppDatabase.getDatabase(application).taskDao();
    }

    @VisibleForTesting
    TaskDialogViewModel(Application application, TaskDao taskDao) {
        super(application);
        this.taskDao = taskDao;
    }

    public LiveData<LocalTime> getDueTime() {
        return dueTime;
    }

    public LiveData<Task> getTaskLiveData() {
        return taskLiveData;
    }

    public LiveData<LocalDate> getDueDate() {
        return dueDate;
    }

    private Task getCurrentTask() {
        Task task = taskLiveData.getValue();
        if (task == null) {
            return new Task();
        }
        return task;
    }

    public void setTask(Task task) {
        taskLiveData.setValue(task);
    }

    public void setTime(int hour, int minute) {
        Task task = getCurrentTask();
        ZonedDateTime time = task.getTime();
        if (time == null) {
            time = ZonedDateTime.now();
        }
        task.setTime(time.withHour(hour).withMinute(minute));
        setTask(task);
    }

    public void setDate(int year, int month, int dayOfMonth) {
        Task task = getCurrentTask();
        ZonedDateTime time = task.getTime();
        if (time == null) {
            time = ZonedDateTime.now();
        }
        task.setTime(time.withYear(year).withMonth(month).withDayOfMonth(dayOfMonth));
        setTask(task);
    }

    public void setText(String text) {
        Task task = getCurrentTask();
        if (text != null && !text.equals(task.getText())) {
            task.setText(text);
            setTask(task);
        }
    }

    public void saveCurrentTask() {
        AppDatabase.databaseWriteExecutor.execute(() -> taskDao.saveTask(getCurrentTask()));
    }
}