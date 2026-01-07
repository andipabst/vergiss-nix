package de.andicodes.vergissnix.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

@Dao
interface TaskDao {
    @Query("select * from Task where timeDone is null order by time")
    fun allTasks(): Flow<List<Task>>

    @Query("select * from Task where timeDone is not null order by time")
    fun doneTasks(): Flow<List<Task>>

    @Query("select * from Task where timeDone is null and (time is null or time <= :until) order by time")
    fun allTasks(until: ZonedDateTime): Flow<List<Task>>

    @Query("select * from Task where id = :id")
    suspend fun getTask(id: Long): Task?

    /**
     * Save a task in the database.
     *
     * @param task the task to save
     * @return the same task, but updated with the id after the insertion
     */
    suspend fun saveTask(task: Task): Task {
        if (task.timeCreated == null) {
            task.timeCreated = ZonedDateTime.now()
        }
        val id = insertTask(task)
        task.id = id
        return task
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Delete
    suspend fun deleteTask(task: Task)
}