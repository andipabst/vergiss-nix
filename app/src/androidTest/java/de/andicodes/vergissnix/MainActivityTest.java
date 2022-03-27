package de.andicodes.vergissnix;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import de.andicodes.vergissnix.data.AppDatabase;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MainActivityTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @BeforeClass
    public static void beforeClass() {
        InstrumentationRegistry.getInstrumentation().getTargetContext().deleteDatabase(AppDatabase.DATABASE_NAME);
    }

    String taskName = "Testaufgabe 123";

    @Test
    public void _10_createTask() {
        onView(withId(R.id.add_task)).perform(click());

        onView(withId(R.id.edit_task_name)).perform(typeText(taskName));
        onView(withId(R.id.action_save)).perform(click());

        onView(withId(R.id.task_list)).check(matches(hasDescendant(withText(taskName))));
    }

    @Test
    public void _20_editTaskText() {
        onView(withId(R.id.task_list)).check(matches(hasDescendant(withText(taskName))));
        onView(withText(taskName)).perform(click());

        onView(withId(R.id.edit_task_name)).check(matches(withText(taskName)));
        onView(withId(R.id.edit_task_name)).perform(typeText("456"));
        onView(withId(R.id.action_save)).perform(click());

        onView(withId(R.id.task_list)).check(matches(hasDescendant(withText(taskName + "456"))));
    }
}