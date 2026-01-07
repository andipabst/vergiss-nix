package de.andicodes.vergissnix.ui.main

import android.app.Application
import android.content.Context
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import de.andicodes.vergissnix.NotificationBroadcastReceiver
import de.andicodes.vergissnix.data.AppDatabase.Companion.getDatabase
import de.andicodes.vergissnix.data.Task
import de.andicodes.vergissnix.data.TaskDao
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime
import java.util.TimeZone

@ExperimentalMaterial3Api
class EditTaskViewModel(application: Application) : AndroidViewModel(application) {
    private val _task = MutableStateFlow<Task?>(Task())
    private val _text = MutableStateFlow<String?>(null)
    private val _time = MutableStateFlow<LocalTime?>(null)
    private val _date = MutableStateFlow<LocalDate?>(null)

    private val taskDao: TaskDao = getDatabase(application)!!.taskDao()

    private val taskObserver = FlowCollector { task: Task? ->
        if (task != null) {
            _text.value = task.text
            _time.value = task.time?.toLocalTime()
            _date.value = task.time?.toLocalDate()
        } else {
            _text.value = null
            _time.value = null
            _date.value = null
        }
    }

    init {
        viewModelScope.launch {
            _task.collect(taskObserver)
        }
    }

    fun loadTaskById(taskId: Long) {
        viewModelScope.launch {
            taskDao.getTask(taskId).let { _task.value = it }
        }
    }

    private fun getTaskToSave(): Task {
        var task = _task.value
        if (task == null) {
            task = Task()
        }
        task.text = _text.value
        task.time = ZonedDateTime.of(_date.value, _time.value, TimeZone.getDefault().toZoneId())
        return task
    }

    fun saveTask(context: Context) {
        viewModelScope.launch {
            val result = taskDao.saveTask(getTaskToSave())
            NotificationBroadcastReceiver.setNotificationAlarm(context, result)
            _task.value = null
        }
    }

    val time = _time.asStateFlow()
    fun setTime(time: LocalTime) {
        this._time.value = time
    }

    val date = _date.asStateFlow()
    fun setDate(date: LocalDate) {
        this._date.value = date
    }

    val originalTask = _task.asStateFlow()

    val text = _text.asStateFlow()
    fun setText(text: String) {
        this._text.value = text
    }
}