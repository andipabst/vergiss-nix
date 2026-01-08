package de.andicodes.vergissnix.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import de.andicodes.vergissnix.data.AppDatabase.Companion.getDatabase
import de.andicodes.vergissnix.data.Task
import de.andicodes.vergissnix.data.TaskDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.util.stream.Collectors
import java.util.stream.Stream

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val _filter = MutableStateFlow(TaskFilter.COMING_WEEK)
    val filter = _filter.asStateFlow()

    private val taskDao: TaskDao = getDatabase(application)!!.taskDao()

    fun currentTasks(): Flow<List<ListEntry>> {
        return filter
            .flatMapLatest { filterValue: TaskFilter ->
                val now = ZonedDateTime.now()
                when (filterValue) {
                    TaskFilter.DONE -> taskDao.doneTasks()
                    TaskFilter.COMING_WEEK -> taskDao.allOpenTasks(now.plusWeeks(1))
                    TaskFilter.COMING_MONTH -> taskDao.allOpenTasks(now.plusMonths(1))
                    TaskFilter.COMING_ALL -> taskDao.allOpenTasks()
                    TaskFilter.ALL -> taskDao.allTasks()
                }
            }
            .map { tasks ->
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
                        } else if (time.isAfter(today.plusDays(2)) && time.isBefore(
                                today.plusWeeks(
                                    1
                                )
                            )
                        ) {
                            return@groupingBy TemporalGrouping.THIS_WEEK
                        } else if (time.isAfter(today.plusWeeks(1)) && time.isBefore(
                                today.plusMonths(
                                    1
                                )
                            )
                        ) {
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
        viewModelScope.launch { taskDao.saveTask(task) }
    }

    fun setFilter(filter: TaskFilter) {
        this._filter.value = filter
    }

    enum class TemporalGrouping {
        OVERDUE, TODAY, TOMORROW, THIS_WEEK, THIS_MONTH, LATER
    }

    sealed class ListEntry(val type: Int) {
        companion object {
            const val HEADER_TYPE = 1
            const val TASK_TYPE = 2
        }

        abstract fun getId(): String
    }

    class HeaderEntry(val temporalGrouping: TemporalGrouping) : ListEntry(HEADER_TYPE) {
        override fun getId(): String = temporalGrouping.name
    }

    class TaskEntry(val task: Task) : ListEntry(TASK_TYPE) {
        override fun getId(): String = task.id.toString()
    }
}