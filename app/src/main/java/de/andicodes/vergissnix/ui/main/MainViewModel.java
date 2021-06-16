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
import de.andicodes.vergissnix.NotificationBroadcastReceiver;
import de.andicodes.vergissnix.data.AppDatabase;
import de.andicodes.vergissnix.data.Task;
import de.andicodes.vergissnix.data.TaskDao;

public class MainViewModel extends AndroidViewModel {
    private final MutableLiveData<Task> editedTaskLiveData = new MutableLiveData<>(new Task());
    private final TaskDao taskDao;

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
        return taskDao.allTasks();
    }

    public void deleteTask(Task task) {
        AppDatabase.databaseWriteExecutor.execute(() -> taskDao.deleteTask(task));
    }

    public LiveData<Task> getEditedTaskLiveData() {
        return editedTaskLiveData;
    }

    private Task getCurrentTask() {
        Task task = editedTaskLiveData.getValue();
        if (task == null) {
            return new Task();
        }
        return task;
    }

    public void setEditedTask(Task task) {
        editedTaskLiveData.setValue(task);
    }

    public void setText(String text) {
        Task task = getCurrentTask();
        if (text != null && !text.equals(task.getText())) {
            task.setText(text);
            setEditedTask(task);
        }
    }

    public void saveTask(Context context, Task task) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Task result = taskDao.saveTask(task);
            NotificationBroadcastReceiver.setNotificationAlarm(context, result);
            editedTaskLiveData.postValue(null);
        });
    }
}