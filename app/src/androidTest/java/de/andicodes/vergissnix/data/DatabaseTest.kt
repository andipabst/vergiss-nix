package de.andicodes.vergissnix.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

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
    fun writeTaskAndReadInList() = runTest {
        val task = Task()
        task.text = "Test Task"
        task.time = ZonedDateTime.parse("2021-11-06T10:29:57+01:00")

        val savedTask = taskDao!!.saveTask(task)
        val tasks = taskDao!!.allTasks().first()
        Assertions.assertThat(tasks)
            .isNotNull()
            .hasSize(1)
        Assertions.assertThat(tasks[0]).usingRecursiveComparison()
            .ignoringFields("timeCreated")
            .isEqualTo(savedTask)
        Assertions.assertThat(tasks[0].timeCreated)
            .isCloseTo(savedTask.timeCreated, Assertions.within(0, ChronoUnit.SECONDS))
    }
}