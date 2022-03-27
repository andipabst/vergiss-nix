package de.andicodes.vergissnix

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import de.andicodes.vergissnix.data.AppDatabase
import org.junit.BeforeClass
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class MainActivityTest {
    @Rule
    var activityRule = ActivityScenarioRule(
        MainActivity::class.java
    )
    var taskName = "Testaufgabe 123"

    @Test
    fun _10_createTask() {
        Espresso.onView(ViewMatchers.withId(R.id.add_task)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.edit_task_name))
            .perform(ViewActions.typeText(taskName))
        Espresso.onView(ViewMatchers.withId(R.id.action_save)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.task_list))
            .check(ViewAssertions.matches(ViewMatchers.hasDescendant(ViewMatchers.withText(taskName))))
    }

    @Test
    fun _20_editTaskText() {
        Espresso.onView(ViewMatchers.withId(R.id.task_list))
            .check(ViewAssertions.matches(ViewMatchers.hasDescendant(ViewMatchers.withText(taskName))))
        Espresso.onView(ViewMatchers.withText(taskName)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.edit_task_name))
            .check(ViewAssertions.matches(ViewMatchers.withText(taskName)))
        Espresso.onView(ViewMatchers.withId(R.id.edit_task_name))
            .perform(ViewActions.typeText("456"))
        Espresso.onView(ViewMatchers.withId(R.id.action_save)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.task_list))
            .check(ViewAssertions.matches(ViewMatchers.hasDescendant(ViewMatchers.withText(taskName + "456"))))
    }

    companion object {
        @BeforeClass
        fun beforeClass() {
            InstrumentationRegistry.getInstrumentation().targetContext.deleteDatabase(AppDatabase.DATABASE_NAME)
        }
    }
}