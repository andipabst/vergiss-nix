package de.andicodes.vergissnix

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.andicodes.vergissnix.ui.main.EditTaskFragment
import de.andicodes.vergissnix.ui.main.MainFragment
import de.andicodes.vergissnix.ui.theme.AppTheme

@ExperimentalMaterial3Api
@ExperimentalLayoutApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Notifications.createNotificationChannel(applicationContext)

        setContent {
            val navController = rememberNavController()

            AppTheme {
                NavHost(navController = navController, startDestination = "taskOverview") {
                    composable("taskOverview") {
                        MainFragment().TaskOverviewScreen(
                            navigateToEditTask = { task -> navController.navigate("editTask/" + task.id) },
                            navigateToCreateTask = { navController.navigate("createTask") }
                        )
                    }
                    composable("editTask/{taskId}") { backStackEntry ->
                        EditTaskFragment().EditTask(
                            taskId = backStackEntry.arguments?.getString("taskId"),
                            navigateUp = { navController.popBackStack() }
                        )
                    }
                    composable("createTask") {
                        EditTaskFragment().EditTask(
                            createTask = true,
                            navigateUp = { navController.popBackStack() }
                        )
                    }
                }
            }

            if (intent.action == Intent.ACTION_VIEW
                && intent.getStringExtra("android.intent.extra.shortcut.ID").equals("add_entry")
            ) {
                navController.navigate("createTask")
            }
        }


    }
}