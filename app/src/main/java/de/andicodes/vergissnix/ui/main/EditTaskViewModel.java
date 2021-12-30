package de.andicodes.vergissnix.ui.main;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import de.andicodes.vergissnix.NotificationBroadcastReceiver;
import de.andicodes.vergissnix.data.AppDatabase;
import de.andicodes.vergissnix.data.Task;
import de.andicodes.vergissnix.data.TaskDao;
import de.andicodes.vergissnix.data.TimeHelper;
import de.andicodes.vergissnix.data.TimeRecommendation;

public class EditTaskViewModel extends AndroidViewModel {
    private final MutableLiveData<Task> task = new MutableLiveData<>(new Task());
    private final MutableLiveData<String> text = new MutableLiveData<>();
    private final MutableLiveData<LocalDateTime> customDatetime = new MutableLiveData<>();
    private final MutableLiveData<LocalDateTime> recommendationDatetime = new MutableLiveData<>();
    private final TaskDao taskDao;

    private final Observer<Task> taskObserver = task -> {
        if (task != null) {
            text.setValue(task.getText());
            setTimeFromTask(task);
        } else {
            text.setValue(null);
            setCustomDatetime(null);
            setRecommendationDatetime(null);
        }
    };

    public EditTaskViewModel(@NonNull Application application) {
        super(application);
        this.taskDao = AppDatabase.getDatabase(application).taskDao();
        task.observeForever(taskObserver);
    }

    @Override
    protected void onCleared() {
        task.removeObserver(taskObserver);
        super.onCleared();
    }

    public Task getTask() {
        Task task = this.task.getValue();
        if (task == null) {
            task = new Task();
        }
        task.setText(text.getValue());
        if (customDatetime.getValue() != null) {
            task.setTime(ZonedDateTime.of(customDatetime.getValue(), TimeZone.getDefault().toZoneId()));
        }
        if (recommendationDatetime.getValue() != null) {
            task.setTime(ZonedDateTime.of(recommendationDatetime.getValue(), TimeZone.getDefault().toZoneId()));
        }
        return task;
    }

    public void setTask(Task task) {
        this.task.setValue(task);
    }

    private void setTimeFromTask(Task task) {
        if (task.getTime() != null) {
            LocalDateTime taskTime = task.getTime().toLocalDateTime();

            for (TimeRecommendation recommendation : TimeHelper.getTimeRecommendations(LocalDateTime.now())) {
                if (recommendation.getDateTime().equals(taskTime)) {
                    recommendationDatetime.setValue(taskTime);
                    return;
                }
            }
            // set custom time if no recommendation matched
            customDatetime.setValue(taskTime);
        } else {
            setCustomDatetime(null);
            setRecommendationDatetime(null);
        }
    }

    public void setCustomDatetime(LocalDateTime customDatetime) {
        this.recommendationDatetime.setValue(null);
        this.customDatetime.setValue(customDatetime);
    }

    public LiveData<LocalDateTime> getCustomDatetime() {
        return customDatetime;
    }

    public void setRecommendationDatetime(LocalDateTime datetime) {
        this.recommendationDatetime.setValue(datetime);
        this.customDatetime.setValue(null);
    }

    public LiveData<LocalDateTime> getRecommendationDatetime() {
        return recommendationDatetime;
    }

    public MutableLiveData<String> getText() {
        return text;
    }

    public void saveCurrentTask(Context context) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Task result = taskDao.saveTask(getTask());
            NotificationBroadcastReceiver.setNotificationAlarm(context, result);
            task.postValue(null);
        });
    }
}