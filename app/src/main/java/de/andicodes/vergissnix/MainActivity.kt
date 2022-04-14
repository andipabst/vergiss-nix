package de.andicodes.vergissnix

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.andicodes.vergissnix.R
import de.andicodes.vergissnix.Notifications
import de.andicodes.vergissnix.ui.main.EditTaskFragment
import de.andicodes.vergissnix.ui.main.MainFragment

@ExperimentalMaterialApi
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Notifications.createNotificationChannel(applicationContext)

        //setContentView(R.layout.main_activity)
        //setSupportActionBar(findViewById(R.id.toolBar))

        setContent {
            val navController = rememberNavController()

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