package de.andicodes.vergissnix.data;

import java.time.ZonedDateTime;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public abstract class TaskDao {

    @Query("select * from Task where timeDone is null order by time")
    public abstract LiveData<List<Task>> allTasks();

    @Query("select * from Task where timeDone is not null order by time")
    public abstract LiveData<List<Task>> doneTasks();

    @Query("select * from Task where timeDone is null and (time is null or time <= :until) order by time")
    public abstract LiveData<List<Task>> allTasks(ZonedDateTime until);

    @Query("select * from Task where id = :id")
    public abstract Task getTask(long id);

    /**
     * Save a task in the database.
     *
     * @param task the task to save
     * @return the same task, but updated with the id after the insertion
     */
    public Task saveTask(Task task) {
        if (task.getTimeCreated() == null) {
            task.setTimeCreated(ZonedDateTime.now());
        }
        long id = insertTask(task);
        task.setId(id);
        return task;
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract long insertTask(Task task);

    @Delete
    public abstract void deleteTask(Task task);
}
