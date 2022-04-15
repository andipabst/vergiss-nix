package de.andicodes.vergissnix.ui.main

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.andicodes.vergissnix.R
import de.andicodes.vergissnix.data.Task
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@ExperimentalMaterialApi
class MainFragment {

    @Composable
    fun TaskOverviewScreen(
        navigateToEditTask: (Task?) -> Unit,
    ) {
        val (showDialog, setShowDialog) = remember { mutableStateOf(false) }
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
            content = {
                if (showDialog) {
                    SelectFilterDialog(onDismiss = {
                        setShowDialog(false)
                    })
                }
                TaskList(navigateToEditTask = navigateToEditTask)
            },
            floatingActionButton = {
                AddTaskButton(navigateToEditTask = navigateToEditTask)
            }
        )
    }

    @Composable
    fun AddTaskButton(
        navigateToEditTask: (Task?) -> Unit
    ) {
        ExtendedFloatingActionButton(
            text = {
                Text(stringResource(id = R.string.add))
            },
            icon = {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = stringResource(id = R.string.add)
                )
            },
            onClick = {
                navigateToEditTask(null)
            }
        )
    }

    @Composable
    fun TaskList(
        viewModel: MainViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
        navigateToEditTask: (Task) -> Unit
    ) {
        val currentTasks = viewModel.currentTasks().observeAsState(initial = emptyList())

        LazyColumn {
            items(currentTasks.value, MainViewModel.ListEntry::getId) { entry ->
                if (entry.type == MainViewModel.ListEntry.HEADER_TYPE) {
                    val header = entry as MainViewModel.HeaderEntry
                    ListHeader(header)
                } else if (entry.type == MainViewModel.ListEntry.TASK_TYPE) {
                    val task = entry as MainViewModel.TaskEntry

                    val dismissState = rememberDismissState(
                        confirmStateChange = {
                            if (it == DismissValue.DismissedToStart) {
                                viewModel.markTaskDone(task.task)
                                // TODO show confirmatino after removing (snackbar)
                                /*Snackbar.make(
                                    requireView(),
                                    R.string.taskDone,
                                    Snackbar.LENGTH_LONG
                                )
                                    .setAction(R.string.undo) {
                                        mainViewModel.markTaskNotDone(task.task)
                                    }
                                    .show()
                                cancelNotification(requireContext(), task.task.id)

                                 */
                            }

                            true
                        }
                    )

                    SwipeToDismiss(
                        state = dismissState,
                        directions = setOf(DismissDirection.EndToStart),
                        background = {
                            dismissState.dismissDirection ?: return@SwipeToDismiss
                            val color by animateColorAsState(
                                when (dismissState.targetValue) {
                                    DismissValue.DismissedToStart -> Color.Green
                                    else -> Color.LightGray
                                }
                            )
                            val scale by animateFloatAsState(
                                if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f
                            )

                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .background(color)
                                    .padding(horizontal = 20.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Icon(
                                    Icons.Default.Done,
                                    contentDescription = stringResource(R.string.done),
                                    modifier = Modifier.scale(scale)
                                )
                            }
                        },
                        dismissContent = {
                            Card(
                                elevation = animateDpAsState(
                                    if (dismissState.dismissDirection != null) 4.dp else 0.dp
                                ).value,
                            ) {
                                ListItem(
                                    text = { Text(task.task.text ?: "??") },
                                    secondaryText = {
                                        task.task.time?.let { time ->
                                            Text(
                                                time.format(
                                                    DateTimeFormatter.ofLocalizedDateTime(
                                                        FormatStyle.SHORT
                                                    )
                                                )
                                            )
                                        }
                                    },
                                    modifier = Modifier.clickable {
                                        navigateToEditTask(task.task)
                                    }
                                )
                                Divider()
                            }
                        }
                    )
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
            color = Color(R.color.secondary),
            modifier = Modifier.padding(8.dp, 12.dp, 8.dp, 4.dp)
        )
    }

    @Composable
    fun SelectFilterDialog(
        viewModel: MainViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
        onDismiss: () -> Unit
    ) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            buttons = {},
            title = {
                Text(stringResource(id = R.string.filter))
            },
            text = {
                val selectedFilter = viewModel.getFilter().observeAsState()

                Column(Modifier.selectableGroup()) {
                    MainViewModel.TaskFilter.values().forEach { filterItem ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .selectable(
                                    selected = (filterItem == selectedFilter.value),
                                    onClick = {
                                        viewModel.setFilter(filterItem)
                                        onDismiss()
                                    },
                                    role = Role.RadioButton
                                )
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (filterItem == selectedFilter.value),
                                onClick = null // null recommended for accessibility with screenreaders
                            )
                            val text = when (filterItem) {
                                MainViewModel.TaskFilter.DONE -> R.string.done
                                MainViewModel.TaskFilter.COMING_WEEK -> R.string.upcoming_week
                                MainViewModel.TaskFilter.COMING_MONTH -> R.string.upcoming_month
                                MainViewModel.TaskFilter.COMING_ALL -> R.string.all_open
                            }
                            Text(
                                text = stringResource(id = text),
                                style = MaterialTheme.typography.body1.merge(),
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }
                }
            }
        )
    }
}