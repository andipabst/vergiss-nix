package de.andicodes.vergissnix.ui.main

import android.app.Application
import android.content.Context
import androidx.compose.material.ExperimentalMaterialApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import de.andicodes.vergissnix.NotificationBroadcastReceiver
import de.andicodes.vergissnix.data.AppDatabase
import de.andicodes.vergissnix.data.AppDatabase.Companion.getDatabase
import de.andicodes.vergissnix.data.Task
import de.andicodes.vergissnix.data.TaskDao
import de.andicodes.vergissnix.data.TimeHelper.getTimeRecommendations
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.util.*

@ExperimentalMaterialApi
class EditTaskViewModel(application: Application) : AndroidViewModel(application) {
    private val task = MutableLiveData(Task())
    val text = MutableLiveData<String?>()
    private val customDatetime = MutableLiveData<LocalDateTime?>()
    private val recommendationDatetime = MutableLiveData<LocalDateTime?>()
    private val taskDao: TaskDao = getDatabase(application)!!.taskDao()
    private val taskObserver = Observer { task: Task? ->
        if (task != null) {
            text.value = task.text
            setTimeFromTask(task)
        } else {
            text.value = null
            setCustomDatetime(null)
            setRecommendationDatetime(null)
        }
    }

    override fun onCleared() {
        task.removeObserver(taskObserver)
        super.onCleared()
    }

    fun getTask(): Task {
        var task = task.value
        if (task == null) {
            task = Task()
        }
        task.text = text.value
        if (customDatetime.value != null) {
            task.time = ZonedDateTime.of(customDatetime.value, TimeZone.getDefault().toZoneId())
        }
        if (recommendationDatetime.value != null) {
            task.time =
                ZonedDateTime.of(recommendationDatetime.value, TimeZone.getDefault().toZoneId())
        }
        return task
    }

    fun setTask(task: Task?) {
        this.task.value = task
    }

    fun setTaskId(taskId: Long) {
        AppDatabase.databaseWriteExecutor.execute {
            val dbTask = taskDao.getTask(taskId)
            this.task.postValue(dbTask)
        }
    }

    private fun setTimeFromTask(task: Task) {
        if (task.time != null) {
            val taskTime = task.time!!.toLocalDateTime()
            for ((_, _, dateTime) in getTimeRecommendations(LocalDateTime.now())) {
                if (dateTime == taskTime) {
                    recommendationDatetime.value = taskTime
                    return
                }
            }
            // set custom time if no recommendation matched
            customDatetime.setValue(taskTime)
        } else {
            setCustomDatetime(null)
            setRecommendationDatetime(null)
        }
    }

    fun setCustomDatetime(customDatetime: LocalDateTime?) {
        recommendationDatetime.value = null
        this.customDatetime.value = customDatetime
    }

    fun getCustomDatetime(): LiveData<LocalDateTime?> {
        return customDatetime
    }

    fun setRecommendationDatetime(datetime: LocalDateTime?) {
        recommendationDatetime.value = datetime
        customDatetime.value = null
    }

    fun getRecommendationDatetime(): LiveData<LocalDateTime?> {
        return recommendationDatetime
    }

    fun saveCurrentTask(context: Context) {
        AppDatabase.databaseWriteExecutor.execute {
            val result = taskDao.saveTask(getTask())
            NotificationBroadcastReceiver.setNotificationAlarm(context, result)
            task.postValue(null)
        }
    }

    init {
        task.observeForever(taskObserver)
    }
}