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

    @Query("select * from Task")
    public abstract LiveData<List<Task>> allTasks();

    @Query("select * from Task where id = :id")
    public abstract Task getTask(long id);

    public void saveTask(Task task) {
        if (task.getTimeCreated() == null) {
            task.setTimeCreated(ZonedDateTime.now());
        }
        insertTask(task);
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract void insertTask(Task task);

    @Delete
    public abstract void deleteTask(Task task);
}
