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
    private final MutableLiveData<LocalDateTime> datetime = new MutableLiveData<>();

    private final Observer<Task> taskObserver = task -> {
        if (task != null) {
            text.setValue(task.getText());
            if (task.getTime() != null) {
                datetime.setValue(task.getTime().toLocalDateTime());
            } else {
                datetime.setValue(null);
            }
        } else {
            text.setValue(null);
            datetime.setValue(null);
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
        if (datetime.getValue() != null) {
            task.setTime(ZonedDateTime.of(datetime.getValue(), TimeZone.getDefault().toZoneId()));
        }
        return task;
    }

    public void setTask(Task task) {
        this.task.setValue(task);
    }

    public void setDatetime(LocalDateTime datetime) {
        this.datetime.setValue(datetime);
    }

    public LiveData<LocalDateTime> getDatetime() {
        return datetime;
    }

    public MutableLiveData<String> getText() {
        return text;
    }
}