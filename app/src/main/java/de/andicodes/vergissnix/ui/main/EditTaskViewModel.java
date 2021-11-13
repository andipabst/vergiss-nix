package de.andicodes.vergissnix.ui.main;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.TimeZone;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import de.andicodes.vergissnix.data.Task;

public class EditTaskViewModel extends ViewModel {
    private final MutableLiveData<Task> task = new MutableLiveData<>(new Task());
    private final MutableLiveData<String> text = new MutableLiveData<>();
    private final MutableLiveData<LocalDateTime> customDatetime = new MutableLiveData<>();
    private final MutableLiveData<LocalDateTime> recommendationDatetime = new MutableLiveData<>();

    private final Observer<Task> taskObserver = task -> {
        if (task != null) {
            text.setValue(task.getText());
            if (task.getTime() != null) {
                setCustomDatetime(task.getTime().toLocalDateTime());
            } else {
                setCustomDatetime(null);
            }
        } else {
            text.setValue(null);
            setCustomDatetime(null);
        }
    };

    public EditTaskViewModel() {
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
}