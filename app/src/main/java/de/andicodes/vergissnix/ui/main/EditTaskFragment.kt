package de.andicodes.vergissnix.ui.main

import android.view.ContextThemeWrapper
import android.widget.CalendarView
import android.widget.TimePicker
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
import de.andicodes.vergissnix.R
import de.andicodes.vergissnix.data.TimeHelper.getTimeRecommendations
import java.time.LocalDate
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
            content = {
                Column(
                    modifier = Modifier.padding(8.dp)
                ) {
                    val text by viewModel.text.observeAsState()
                    val recommendationDateTime by viewModel.getRecommendationDatetime()
                        .observeAsState()
                    val selectedCustomDateTime by viewModel.getCustomDatetime().observeAsState()
                    val datesFromText by viewModel.possibleDates.observeAsState()
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

                    datesFromText?.forEach { dateTime ->
                        Text(text = dateTime.toString())
                    }

                    ChipGroup(
                        selectedRecommendation = recommendationDateTime,
                        selectedCustom = selectedCustomDateTime,
                        selectionRecommendationChangedListener = {
                            viewModel.setRecommendationDatetime(it)
                        },
                        selectionCustomChangedListener = { viewModel.setCustomDatetime(it) }
                    )

                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        modifier = Modifier
                            .height(IntrinsicSize.Min)
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        SelectionToggleButton(
                            text = stringResource(R.string.chooseDate),
                            modifier = Modifier.weight(1f),
                            selected = showDateSelection,
                            onToggle = { showDateSelection = !showDateSelection }
                        )
                        Divider(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(1.dp)
                        )
                        SelectionToggleButton(
                            text = stringResource(R.string.chooseTime),
                            modifier = Modifier.weight(1f),
                            selected = showTimeSelection,
                            onToggle = { showTimeSelection = !showTimeSelection }
                        )
                    }

                    if (showDateSelection) {
                        CalendarViewWrapper(
                            onDateSelected = { }
                        )
                    }

                    if (showTimeSelection) {
                        TimePickerWrapper(
                            onTimeSelected = {}
                        )
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
    @Preview
    fun CalendarViewWrapper(onDateSelected: (LocalDate) -> Unit = {}) {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            factory = { context ->
                CalendarView(ContextThemeWrapper(context, R.style.CalenderViewCustom))
            },
            update = { view ->
                view.setOnDateChangeListener { _, year, month, dayOfMonth ->
                    onDateSelected(LocalDate.of(year, month + 1, dayOfMonth))
                }
            }
        )
    }

    @Composable
    @Preview
    fun TimePickerWrapper(onTimeSelected: (LocalTime) -> Unit = {}) {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            factory = { context ->
                TimePicker(ContextThemeWrapper(context, R.style.TimePickerViewCustom))
            },
            update = { view ->
                view.setOnTimeChangedListener { _, hourOfDay, minute ->
                    onTimeSelected(LocalTime.of(hourOfDay, minute))
                }
            }
        )
    }


    companion object {
        val DEFAULT_TIME: LocalTime = LocalTime.of(9, 0)
    }
}