package de.andicodes.vergissnix.ui.main;

import android.app.Application;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import de.andicodes.vergissnix.data.AppDatabase;
import de.andicodes.vergissnix.data.Task;
import de.andicodes.vergissnix.data.TaskDao;

public class MainViewModel extends AndroidViewModel {

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
}