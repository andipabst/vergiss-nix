package de.andicodes.vergissnix.data;

import android.content.Context;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(AndroidJUnit4.class)
public class DatabaseTest {
    private TaskDao taskDao;
    private AppDatabase db;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        taskDao = db.taskDao();
    }

    @After
    public void closeDb() {
        db.close();
    }

    @Test
    public void writeTaskAndReadInList() throws Exception {
        ZonedDateTime time = ZonedDateTime.of(2021, 11, 6, 10, 29, 57, 632, ZoneOffset.ofHours(1));
        Task task = new Task();
        task.setText("Test Task");
        task.setTime(time);

        Task savedTask = taskDao.saveTask(task);

        List<Task> tasks = taskDao.allTasks().getValue();
        assertThat(tasks).containsExactly(savedTask);
    }
}