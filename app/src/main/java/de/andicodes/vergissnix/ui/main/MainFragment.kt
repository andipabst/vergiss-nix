package de.andicodes.vergissnix.ui.main

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import de.andicodes.vergissnix.Notifications
import de.andicodes.vergissnix.R
import de.andicodes.vergissnix.data.Task
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@ExperimentalMaterial3Api
class MainFragment {

    @Composable
    fun TaskOverviewScreen(
        navigateToEditTask: (Task) -> Unit,
        navigateToCreateTask: () -> Unit,
    ) {
        val (showDialog, setShowDialog) = remember { mutableStateOf(false) }
        val snackbarHostState = remember { SnackbarHostState() }
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(id = R.string.app_name)) },
                    actions = {
                        IconButton(onClick = {
                            setShowDialog(true)
                        }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_baseline_filter_alt_24),
                                contentDescription = stringResource(R.string.filter)
                            )
                        }
                    }
                )
            },
            content = { paddingValues ->
                if (showDialog) {
                    SelectFilterDialog(onDismiss = {
                        setShowDialog(false)
                    })
                }
                TaskList(
                    navigateToEditTask = navigateToEditTask,
                    modifier = Modifier.padding(paddingValues),
                    snackbarHostState = snackbarHostState
                )
            },
            floatingActionButton = {
                AddTaskButton(navigateToCreateTask = navigateToCreateTask)
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            }
        )
    }

    @Composable
    fun AddTaskButton(
        navigateToCreateTask: () -> Unit
    ) {
        ExtendedFloatingActionButton(
            text = {
                Text(stringResource(id = R.string.add))
            },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.baseline_add_24),
                    contentDescription = stringResource(id = R.string.add)
                )
            },
            onClick = {
                navigateToCreateTask()
            }
        )
    }

    @Composable
    fun TaskList(
        viewModel: MainViewModel = viewModel(),
        modifier: Modifier,
        navigateToEditTask: (Task) -> Unit,
        snackbarHostState: SnackbarHostState,
    ) {
        val currentTasks =
            viewModel.currentTasks().collectAsStateWithLifecycle(initialValue = emptyList())
        val scope = rememberCoroutineScope()
        val context = LocalContext.current

        LazyColumn(modifier = modifier) {
            items(currentTasks.value, MainViewModel.ListEntry::getId) { entry ->
                if (entry.type == MainViewModel.ListEntry.HEADER_TYPE) {
                    val header = entry as MainViewModel.HeaderEntry
                    ListHeader(header)
                } else if (entry.type == MainViewModel.ListEntry.TASK_TYPE) {
                    val task = entry as MainViewModel.TaskEntry

                    val dismissState = rememberSwipeToDismissBoxState()
                    var visible by remember { mutableStateOf(true) }

                    if (visible) {
                        SwipeToDismissBox(
                            state = dismissState,
                            enableDismissFromStartToEnd = false,
                            enableDismissFromEndToStart = task.task.timeDone == null,
                            modifier = Modifier.animateItem(),
                            backgroundContent = {
                                val color by animateColorAsState(
                                    when (dismissState.targetValue) {
                                        SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.primary
                                        else -> Color.LightGray
                                    }
                                )
                                val scale by animateFloatAsState(
                                    when (dismissState.targetValue) {
                                        SwipeToDismissBoxValue.Settled -> 0.75f
                                        else -> 1f
                                    }
                                )

                                Box(
                                    Modifier
                                        .fillMaxSize()
                                        .background(color)
                                        .padding(horizontal = 20.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.check_24),
                                        contentDescription = stringResource(R.string.done),
                                        modifier = Modifier.scale(scale)
                                    )
                                }
                            },
                            onDismiss = {
                                if (it == SwipeToDismissBoxValue.EndToStart) {
                                    scope.launch {
                                        visible = false

                                        val snackbarResult = snackbarHostState.showSnackbar(
                                            message = getString(context, R.string.taskDone),
                                            actionLabel = getString(context, R.string.undo),
                                            duration = SnackbarDuration.Long,
                                        )
                                        when (snackbarResult) {
                                            SnackbarResult.ActionPerformed -> {
                                                scope.launch {
                                                    dismissState.reset()
                                                    visible = true
                                                }
                                            }

                                            else -> {
                                                viewModel.markTaskDone(task.task)
                                                Notifications.cancelNotification(
                                                    context,
                                                    task.task.id
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    scope.launch { dismissState.reset() }
                                }
                            },
                            content = {
                                Card(shape = RoundedCornerShape(0)) {
                                    TaskInList(
                                        task = task.task,
                                        navigateToEditTask = navigateToEditTask,
                                    )
                                    HorizontalDivider()
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    @Preview
    @Composable
    fun ListHeader(
        header: MainViewModel.HeaderEntry = MainViewModel.HeaderEntry(MainViewModel.TemporalGrouping.TODAY)
    ) {
        val text = when (header.temporalGrouping) {
            MainViewModel.TemporalGrouping.OVERDUE -> R.string.overdue
            MainViewModel.TemporalGrouping.TODAY -> R.string.today
            MainViewModel.TemporalGrouping.TOMORROW -> (R.string.tomorrow)
            MainViewModel.TemporalGrouping.THIS_WEEK -> (R.string.this_week)
            MainViewModel.TemporalGrouping.THIS_MONTH -> (R.string.this_month)
            MainViewModel.TemporalGrouping.LATER -> (R.string.later)
        }

        Text(
            stringResource(text),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(8.dp, 12.dp, 8.dp, 4.dp)
        )
    }

    @Composable
    fun TaskInList(
        task: Task,
        navigateToEditTask: (Task) -> Unit
    ) {
        ListItem(
            headlineContent = { Text(task.text ?: "??") },
            supportingContent = {
                task.time?.let { time ->
                    Text(
                        text = time.format(
                            DateTimeFormatter.ofLocalizedDateTime(
                                FormatStyle.SHORT
                            )
                        ) + (task.timeDone?.let { timeDone ->
                            " - " + stringResource(R.string.done) + ": " + timeDone.format(
                                DateTimeFormatter.ofLocalizedDateTime(
                                    FormatStyle.SHORT
                                )
                            )
                        } ?: ""),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            },
            trailingContent = {
                if (task.timeDone != null) {
                    Icon(
                        painter = painterResource(R.drawable.check_24),
                        contentDescription = stringResource(R.string.done),
                    )
                }
            },
            modifier = Modifier
                .clickable {
                    navigateToEditTask(task)
                }
        )
    }

    @Preview
    @Composable
    fun TaskInListPreview() {
        TaskInList(
            task = Task(
                time = ZonedDateTime.of(2023, 2, 4, 11, 10, 0, 0, ZoneId.systemDefault()),
                text = "Create a reminder app"
            ),
            navigateToEditTask = {}
        )
    }

    @Composable
    fun SelectFilterDialog(
        viewModel: MainViewModel = viewModel(),
        onDismiss: () -> Unit
    ) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            confirmButton = {
                TextButton(onClick = { onDismiss() }) {
                    Text(stringResource(id = android.R.string.ok))
                }
            },
            title = {
                Text(stringResource(id = R.string.filter))
            },
            text = {
                val selectedFilter by viewModel.filter.collectAsStateWithLifecycle()

                Column(Modifier.selectableGroup()) {
                    TaskFilter.entries.forEach { filterItem ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = (filterItem == selectedFilter),
                                    onClick = {
                                        viewModel.setFilter(filterItem)
                                        onDismiss()
                                    },
                                    role = Role.RadioButton
                                )
                                .padding(horizontal = 4.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (filterItem == selectedFilter),
                                onClick = null // null recommended for accessibility with screenreaders
                            )
                            val text = when (filterItem) {
                                TaskFilter.DONE -> R.string.done
                                TaskFilter.COMING_WEEK -> R.string.upcoming_week
                                TaskFilter.COMING_MONTH -> R.string.upcoming_month
                                TaskFilter.COMING_ALL -> R.string.all_open
                            }
                            Text(
                                text = stringResource(id = text),
                                style = MaterialTheme.typography.bodyMedium.merge(),
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }
                }
            }
        )
    }
}