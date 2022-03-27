package de.andicodes.vergissnix.ui.main

import android.app.Application
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import de.andicodes.vergissnix.data.AppDatabase
import de.andicodes.vergissnix.data.AppDatabase.Companion.getDatabase
import de.andicodes.vergissnix.data.Task
import de.andicodes.vergissnix.data.TaskDao
import java.time.ZonedDateTime

class MainViewModel : AndroidViewModel {
    private val filter = MutableLiveData(TaskFilter.COMING_WEEK)
    private val taskDao: TaskDao

    enum class TaskFilter(val position: Int) {
        DONE(0), COMING_WEEK(1), COMING_MONTH(2), COMING_ALL(3);

        companion object {
            @JvmStatic
            fun of(position: Int): TaskFilter {
                for (filter in values()) {
                    if (filter.position == position) {
                        return filter
                    }
                }
                return COMING_WEEK
            }
        }
    }

    constructor(application: Application) : super(application) {
        taskDao = getDatabase(application)!!.taskDao()
    }

    @VisibleForTesting
    internal constructor(application: Application?, taskDao: TaskDao) : super(application!!) {
        this.taskDao = taskDao
    }

    fun currentTasks(): LiveData<List<Task>> {
        return Transformations.switchMap(filter) { filterValue: TaskFilter? ->
            if (filterValue == null) {
                return@switchMap taskDao.allTasks(ZonedDateTime.now().plusWeeks(1))
            }
            when (filterValue) {
                TaskFilter.DONE -> return@switchMap taskDao.doneTasks()
                TaskFilter.COMING_WEEK -> return@switchMap taskDao.allTasks(
                    ZonedDateTime.now().plusWeeks(1)
                )
                TaskFilter.COMING_MONTH -> return@switchMap taskDao.allTasks(
                    ZonedDateTime.now().plusMonths(1)
                )
                TaskFilter.COMING_ALL -> return@switchMap taskDao.allTasks()
                else -> return@switchMap taskDao.allTasks(ZonedDateTime.now().plusWeeks(1))
            }
        }
    }

    fun markTaskDone(task: Task) {
        task.timeDone = ZonedDateTime.now()
        AppDatabase.databaseWriteExecutor.execute { taskDao.saveTask(task) }
    }

    fun markTaskNotDone(task: Task) {
        task.timeDone = null
        AppDatabase.databaseWriteExecutor.execute { taskDao.saveTask(task) }
    }

    fun getFilter(): TaskFilter? {
        return filter.value
    }

    fun setFilter(filter: TaskFilter) {
        this.filter.value = filter
    }
}