package de.andicodes.vergissnix.ui.main

import android.app.Application
import android.content.Context
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import de.andicodes.vergissnix.NotificationBroadcastReceiver
import de.andicodes.vergissnix.data.AppDatabase
import de.andicodes.vergissnix.data.AppDatabase.Companion.getDatabase
import de.andicodes.vergissnix.data.Task
import de.andicodes.vergissnix.data.TaskDao
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime
import java.util.TimeZone

@ExperimentalMaterial3Api
@ExperimentalLayoutApi
class EditTaskViewModel(application: Application) : AndroidViewModel(application) {
    private val task = MutableLiveData(Task())
    private val text = MutableLiveData<String?>()
    private val time = MutableLiveData<LocalTime?>()
    private val date = MutableLiveData<LocalDate?>()
    private val taskDao: TaskDao = getDatabase(application)!!.taskDao()

    private val taskObserver = Observer { task: Task? ->
        if (task != null) {
            text.value = task.text
            time.value = task.time?.toLocalTime()
            date.value = task.time?.toLocalDate()
        } else {
            text.value = null
            time.value = null
            date.value = null
        }
    }

    init {
        task.observeForever(taskObserver)
    }

    override fun onCleared() {
        task.removeObserver(taskObserver)
        super.onCleared()
    }

    fun loadTaskById(taskId: Long) {
        AppDatabase.databaseWriteExecutor.execute {
            val dbTask = taskDao.getTask(taskId)
            this.task.postValue(dbTask)
        }
    }

    private fun getTaskToSave(): Task {
        var task = task.value
        if (task == null) {
            task = Task()
        }
        task.text = text.value
        task.time = ZonedDateTime.of(date.value, time.value, TimeZone.getDefault().toZoneId())
        return task
    }

    fun saveTask(context: Context) {
        AppDatabase.databaseWriteExecutor.execute {
            val result = taskDao.saveTask(getTaskToSave())
            NotificationBroadcastReceiver.setNotificationAlarm(context, result)
            task.postValue(null)
        }
    }

    fun setTime(time: LocalTime) {
        this.time.value = time
    }

    fun getTime(): LiveData<LocalTime?> {
        return time
    }

    fun setDate(date: LocalDate) {
        this.date.value = date
    }

    fun getDate(): LiveData<LocalDate?> {
        return date
    }

    fun getOriginalTask(): LiveData<Task?> {
        return task
    }

    fun getText(): LiveData<String?> {
        return text
    }

    fun setText(text: String) {
        this.text.value = text
    }
}