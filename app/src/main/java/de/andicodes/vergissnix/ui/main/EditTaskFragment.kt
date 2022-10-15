package de.andicodes.vergissnix.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import de.andicodes.vergissnix.R
import de.andicodes.vergissnix.data.TimeHelper.getTimeRecommendations
import java.time.LocalDateTime
import java.time.LocalTime

@ExperimentalMaterialApi
class EditTaskFragment {

    @Composable
    fun EditTask(
        viewModel: EditTaskViewModel = viewModel(),
        taskId: String?,
        navigateUp: () -> Unit
    ) {
        taskId?.let {
            taskId.toLongOrNull()?.let { id ->
                viewModel.setTaskId(id)
            }
        }
        val context = LocalContext.current

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(stringResource(if (taskId == null) R.string.add else R.string.edit_task))
                    },
                    navigationIcon = {
                        IconButton(onClick = { navigateUp() }) {
                            Icon(
                                Icons.Filled.Close,
                                contentDescription = stringResource(R.string.close)
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                viewModel.saveCurrentTask(context)
                                navigateUp()
                            }
                        ) {
                            Icon(
                                Icons.Filled.Done,
                                contentDescription = stringResource(R.string.save)
                            )
                        }
                    }
                )
            },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .padding(bottom = paddingValues.calculateBottomPadding())
                ) {
                    val text by viewModel.text.observeAsState()
                    val recommendationDateTime by viewModel.getRecommendationDatetime()
                        .observeAsState()
                    val selectedCustomDateTime by viewModel.getCustomDatetime().observeAsState()

                    var showDateSelection by remember { mutableStateOf(false) }
                    var showTimeSelection by remember { mutableStateOf(false) }

                    OutlinedTextField(
                        value = text ?: "",
                        onValueChange = { viewModel.text.value = it },
                        label = { Text(stringResource(R.string.task_name)) },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Send,
                            keyboardType = KeyboardType.Text
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                    )

                    /*ChipGroup(
                        selectedRecommendation = recommendationDateTime,
                        selectedCustom = selectedCustomDateTime,
                        selectionRecommendationChangedListener = {
                            viewModel.setRecommendationDatetime(it)
                        },
                        selectionCustomChangedListener = { viewModel.setCustomDatetime(it) }
                    )*/

                    ShowTimePicker(initHour = 10, initMinute = 12) { time ->

                    }

                }
            }
        )
    }

    @Preview()
    @Composable
    fun ChipGroup(
        selectedRecommendation: LocalDateTime? = null,
        selectionRecommendationChangedListener: (LocalDateTime) -> Unit = {},
        selectedCustom: LocalDateTime? = null,
        selectionCustomChangedListener: (LocalDateTime) -> Unit = {},
    ) {
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { context ->
                TimeRecommendationChips(context).apply {
                    this.timeRecommendations = getTimeRecommendations(LocalDateTime.now())
                    this.selectionRecommendationChangedListener =
                        selectionRecommendationChangedListener
                    this.selectedRecommendation = selectedRecommendation
                }
            },
            update = { view ->
                view.selectedRecommendation = selectedRecommendation
            }
        )
    }

    @Composable
    fun SelectionToggleButton(
        modifier: Modifier = Modifier,
        text: String = "Datum",
        selected: Boolean = false,
        onToggle: () -> Unit = {}
    ) {
        TextButton(
            modifier = modifier,
            onClick = { onToggle() },
            contentPadding = PaddingValues(
                start = 20.dp,
                top = 12.dp,
                end = 20.dp,
                bottom = 12.dp
            )
        ) {
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(text)
            Icon(
                Icons.Filled.ArrowDropDown,
                contentDescription = "More",
                modifier = Modifier
                    .size(ButtonDefaults.IconSize)
                    .rotate(if (selected) 180f else 0f)
            )
        }
    }

    @Composable
    fun ShowTimePicker(initHour: Int, initMinute: Int, onTimeChange: (LocalTime) -> Unit = {}) {
        val dialogState = rememberMaterialDialogState()
        MaterialDialog(
            dialogState = dialogState,
            buttons = {
                positiveButton("Ok")
                negativeButton("Cancel")
            }
        ) {
            timepicker { time ->
                onTimeChange(time)
            }
        }

        Button(onClick = {
            dialogState.show()
        }) {
            Text(text = "Open Time Picker")
        }
    }


    companion object {
        val DEFAULT_TIME: LocalTime = LocalTime.of(9, 0)
    }
}