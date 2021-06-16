package de.andicodes.vergissnix.ui.main;

import java.time.LocalDate;
import java.time.LocalTime;
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
    private final MutableLiveData<LocalDate> date = new MutableLiveData<>();
    private final MutableLiveData<LocalTime> time = new MutableLiveData<>();

    private final Observer<Task> taskObserver = task -> {
        if (task != null) {
            text.setValue(task.getText());
            if (task.getTime() != null) {
                date.setValue(task.getTime().toLocalDate());
                time.setValue(task.getTime().toLocalTime());
            } else {
                date.setValue(null);
                time.setValue(null);
            }
        } else {
            text.setValue(null);
            date.setValue(null);
            time.setValue(null);
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
        task.setTime(ZonedDateTime.of(date.getValue(), time.getValue(), TimeZone.getDefault().toZoneId()));
        return task;
    }

    public void setTask(Task task) {
        this.task.setValue(task);
    }

    public LiveData<LocalTime> getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time.setValue(time);
    }

    public void setDate(LocalDate date) {
        this.date.setValue(date);
    }

    public LiveData<LocalDate> getDate() {
        return date;
    }

    public MutableLiveData<String> getText() {
        return text;
    }
}