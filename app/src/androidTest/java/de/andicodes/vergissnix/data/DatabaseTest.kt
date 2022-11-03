package de.andicodes.vergissnix.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.andicodes.vergissnix.data.AppDatabase
import org.assertj.core.api.Assertions
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.ZoneOffset
import java.time.ZonedDateTime

@RunWith(AndroidJUnit4::class)
class DatabaseTest {
    private var taskDao: TaskDao? = null
    private var db: AppDatabase? = null

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        taskDao = db!!.taskDao()
    }

    @After
    fun closeDb() {
        db!!.close()
    }

    @Test
    fun writeTaskAndReadInList() {
        val task = Task()
        task.text = "Test Task"
        task.time = ZonedDateTime.parse("2021-11-06T10:29:57+01:00")
        AppDatabase.databaseWriteExecutor.execute {
            val savedTask = taskDao!!.saveTask(task)
            val tasks = taskDao!!.allTasks().value
            Assertions.assertThat(tasks)
                .isNotNull
                .containsExactly(savedTask)
        }
    }
}