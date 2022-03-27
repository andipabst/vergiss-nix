package de.andicodes.vergissnix.data

import androidx.lifecycle.LiveData
import androidx.room.*
import java.time.ZonedDateTime

@Dao
abstract class TaskDao {
    @Query("select * from Task where timeDone is null order by time")
    abstract fun allTasks(): LiveData<List<Task>>

    @Query("select * from Task where timeDone is not null order by time")
    abstract fun doneTasks(): LiveData<List<Task>>

    @Query("select * from Task where timeDone is null and (time is null or time <= :until) order by time")
    abstract fun allTasks(until: ZonedDateTime): LiveData<List<Task>>

    @Query("select * from Task where id = :id")
    abstract fun getTask(id: Long): Task?

    /**
     * Save a task in the database.
     *
     * @param task the task to save
     * @return the same task, but updated with the id after the insertion
     */
    fun saveTask(task: Task): Task {
        if (task.timeCreated == null) {
            task.timeCreated = ZonedDateTime.now()
        }
        val id = insertTask(task)
        task.id = id
        return task
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertTask(task: Task): Long

    @Delete
    abstract fun deleteTask(task: Task)
}