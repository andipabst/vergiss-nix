package de.andicodes.vergissnix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.andicodes.vergissnix.ui.main.ColorScheme
import de.andicodes.vergissnix.ui.main.EditTaskFragment
import de.andicodes.vergissnix.ui.main.MainFragment

@ExperimentalMaterialApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Notifications.createNotificationChannel(applicationContext)

        setContent {
            val navController = rememberNavController()

            MaterialTheme(
                colors = ColorScheme
            ) {
                NavHost(navController = navController, startDestination = "taskOverview") {
                    composable("taskOverview") {
                        MainFragment().TaskOverviewScreen(
                            navigateToEditTask = { task -> navController.navigate("editTask/" + task?.id) }
                        )
                    }
                    composable("editTask/{taskId}") { backStackEntry ->
                        EditTaskFragment().EditTask(
                            taskId = backStackEntry.arguments?.getString(
                                "taskId"
                            ),
                            navigateUp = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}