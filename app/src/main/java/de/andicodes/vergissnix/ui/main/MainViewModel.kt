package de.andicodes.vergissnix.ui.main

import android.app.Application
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.*
import de.andicodes.vergissnix.data.AppDatabase
import de.andicodes.vergissnix.data.AppDatabase.Companion.getDatabase
import de.andicodes.vergissnix.data.Task
import de.andicodes.vergissnix.data.TaskDao
import java.time.ZonedDateTime
import java.util.stream.Collectors
import java.util.stream.Stream

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

    fun currentTasks(): LiveData<List<ListEntry>> {
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
        }.map { tasks ->
            val now = ZonedDateTime.now()
            val today = now.withHour(0).withMinute(0).withSecond(0).withNano(0)
            return@map tasks.stream()
                .collect(Collectors.groupingBy { task ->
                    val time = task?.time
                    if (time == null || time.isBefore(today)) {
                        return@groupingBy TemporalGrouping.OVERDUE
                    } else if (time.isAfter(today) && time.isBefore(today.plusDays(1))) {
                        return@groupingBy TemporalGrouping.TODAY
                    } else if (time.isAfter(today.plusDays(1)) && time.isBefore(today.plusDays(2))) {
                        return@groupingBy TemporalGrouping.TOMORROW
                    } else if (time.isAfter(today.plusDays(2)) && time.isBefore(today.plusWeeks(1))) {
                        return@groupingBy TemporalGrouping.THIS_WEEK
                    } else if (time.isAfter(today.plusWeeks(1)) && time.isBefore(today.plusMonths(1))) {
                        return@groupingBy TemporalGrouping.THIS_MONTH
                    } else {
                        return@groupingBy TemporalGrouping.LATER
                    }
                })
                .entries
                .stream()
                .sorted(java.util.Map.Entry.comparingByKey())
                .flatMap { (key, value) ->
                    Stream.concat(
                        Stream.of(HeaderEntry(key)),
                        value.stream().map { task -> TaskEntry(task) })
                }
                .collect(Collectors.toList())

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

    fun getFilter(): MutableLiveData<TaskFilter> {
        return filter
    }

    fun setFilter(filter: TaskFilter) {
        this.filter.value = filter
    }

    enum class TemporalGrouping {
        OVERDUE, TODAY, TOMORROW, THIS_WEEK, THIS_MONTH, LATER
    }

    abstract class ListEntry(val type: Int) {
        companion object {
            const val HEADER_TYPE = 1
            const val TASK_TYPE = 2
        }
    }

    class HeaderEntry(val temporalGrouping: TemporalGrouping) : ListEntry(HEADER_TYPE)
    class TaskEntry(val task: Task) : ListEntry(TASK_TYPE)
}