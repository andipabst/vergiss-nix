package de.andicodes.vergissnix.ui.main


import android.text.format.DateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.viewmodel.compose.viewModel
import de.andicodes.vergissnix.R
import de.andicodes.vergissnix.data.TimeHelper
import de.andicodes.vergissnix.data.TimeHelper.localDateOfEpochMillis
import de.andicodes.vergissnix.data.toEpochMillis
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit
import java.time.temporal.Temporal

@ExperimentalMaterial3Api
@ExperimentalLayoutApi
class EditTaskFragment {

    @Composable
    fun EditTask(
        viewModel: EditTaskViewModel = viewModel(),
        taskId: String? = null,
        createTask: Boolean = false,
        navigateUp: () -> Unit
    ) {
        taskId?.let {
            taskId.toLongOrNull()?.let { id ->
                viewModel.loadTaskById(id)
            }
        }
        val context = LocalContext.current
        val text by viewModel.getText().observeAsState()
        val selectedTime by viewModel.getTime().observeAsState()
        val selectedDate by viewModel.getDate().observeAsState()
        val originalTask by viewModel.getOriginalTask().observeAsState()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(stringResource(if (createTask) R.string.add else R.string.edit_task))
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
                                viewModel.saveTask(context)
                                navigateUp()
                            },
                            enabled = text?.isNotBlank() == true && selectedTime != null && selectedDate != null
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
                        .padding(
                            start = 16.dp,
                            top = paddingValues.calculateTopPadding(),
                            end = 16.dp,
                            bottom = paddingValues.calculateBottomPadding()
                        )
                ) {

                    TitleInput(
                        text = text ?: "",
                        onTextChange = { viewModel.setText(it) },
                        placeholder = stringResource(R.string.task_name)
                    )
                    HorizontalDivider(modifier = Modifier.padding(top = 16.dp))

                    SectionHeadline(
                        text = stringResource(R.string.date),
                        icon = painterResource(id = R.drawable.ic_baseline_calendar_today_24)
                    )

                    FlowRow {
                        val originalDate = originalTask?.time?.toLocalDate()
                        TimeHelper.getDateRecommendations(originalDate, selectedDate)
                            .forEach { dateRecommendation ->
                                DateRecommendationChip(
                                    date = dateRecommendation,
                                    currentlySelectedDate = selectedDate,
                                    onSelected = { viewModel.setDate(it) }
                                )
                            }

                        ShowDatePicker(
                            initialDate = originalDate ?: LocalDate.now(),
                        ) {
                            viewModel.setDate(it)
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(top = 16.dp))

                    SectionHeadline(
                        text = stringResource(R.string.time),
                        icon = painterResource(id = R.drawable.ic_baseline_access_time_24)
                    )

                    FlowRow {
                        val originalTime = originalTask?.time?.toLocalTime()
                        TimeHelper.getTimeRecommendations(originalTime, selectedTime)
                            .forEach { timeRecommendation ->
                                TimeRecommendationChip(
                                    time = timeRecommendation,
                                    currentlySelectedTime = selectedTime,
                                    onSelected = { viewModel.setTime(it) }
                                )
                            }

                        val is24HourFormat by rememberUpdatedState(DateFormat.is24HourFormat(context))

                        ShowTimePicker(
                            initialTime = originalTime ?: LocalTime.now().plusHours(1)
                                .withMinute(0),
                            use24hour = is24HourFormat
                        ) {
                            viewModel.setTime(it)
                        }
                    }
                }
            }
        )
    }

    @Composable
    fun <T> RecommendationChip(
        label: String,
        value: T,
        onSelected: (T) -> Unit,
        selected: Boolean
    ) where T : Temporal {
        FilterChip(
            selected = selected,
            onClick = { onSelected(value) },
            label = { Text(label) },
            modifier = Modifier.padding(end = 8.dp)
        )
    }

    @Composable
    fun TimeRecommendationChip(
        time: LocalTime,
        currentlySelectedTime: LocalTime?,
        onSelected: (LocalTime) -> Unit
    ) {
        val truncatedTime = time.truncatedTo(ChronoUnit.MINUTES)
        val truncatedCurrentlySelectedTime = currentlySelectedTime?.truncatedTo(ChronoUnit.MINUTES)
        RecommendationChip(
            label = truncatedTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)),
            value = truncatedTime,
            onSelected = onSelected,
            selected = truncatedCurrentlySelectedTime == truncatedTime
        )
    }

    @Composable
    fun DateRecommendationChip(
        date: LocalDate,
        currentlySelectedDate: LocalDate?,
        onSelected: (LocalDate) -> Unit,
        today: LocalDate = LocalDate.now(),
    ) {
        val formattedDate = date.format(DateTimeFormatter.ofPattern("dd.MM."))
        val context = LocalContext.current
        val shortDateName = when (date) {
            today -> getString(context, R.string.today)
            today.plusDays(1) -> getString(context, R.string.tomorrow)
            else -> date.format(DateTimeFormatter.ofPattern("E"))
        }

        RecommendationChip(
            label = "$formattedDate ($shortDateName)",
            value = date,
            onSelected = onSelected,
            selected = currentlySelectedDate == date
        )
    }

    @Composable
    fun SectionHeadline(
        text: String,
        icon: Painter
    ) {
        Row(
            modifier = Modifier.padding(top = 20.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = text,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }

    @Composable
    fun TitleInput(
        text: String,
        onTextChange: (String) -> Unit,
        placeholder: String
    ) {
        val textStyle = MaterialTheme.typography.titleLarge.copy(
            color = MaterialTheme.colorScheme.onBackground
        )

        Box(modifier = Modifier.padding(top = 8.dp)) {
            BasicTextField(
                value = text,
                onValueChange = onTextChange,
                textStyle = textStyle,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Send,
                    keyboardType = KeyboardType.Text
                ),
                modifier = Modifier
                    .fillMaxWidth()
            )
            if (text.isEmpty()) {
                Text(
                    text = placeholder,
                    style = textStyle
                )
            }
        }

    }

    @Composable
    fun ShowTimePicker(
        initialTime: LocalTime,
        use24hour: Boolean,
        onTimeChange: (LocalTime) -> Unit = {}
    ) {
        var showTimePicker by remember { mutableStateOf(false) }
        val state = rememberTimePickerState(
            is24Hour = use24hour,
            initialHour = initialTime.hour,
            initialMinute = initialTime.minute
        )
        if (showTimePicker) {
            TimePickerDialog(state, onCancel = { showTimePicker = false }) {
                onTimeChange(LocalTime.of(state.hour, state.minute))
                showTimePicker = false
            }
        }

        FilterChip(
            selected = false,
            onClick = { showTimePicker = true },
            label = { Text(stringResource(R.string.custom)) },
            modifier = Modifier.padding(end = 8.dp)
        )
    }

    @Composable
    fun TimePickerDialog(
        state: TimePickerState,
        onCancel: () -> Unit = {},
        onConfirm: () -> Unit = {}
    ) {
        Dialog(
            onDismissRequest = onCancel,
            properties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                tonalElevation = 6.dp,
                modifier = Modifier
                    .width(328.dp)
                    .background(
                        shape = MaterialTheme.shapes.extraLarge,
                        color = MaterialTheme.colorScheme.surface
                    ),
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp),
                        text = stringResource(R.string.chooseTime),
                        style = MaterialTheme.typography.titleMedium
                    )
                    TimePicker(state)
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Spacer(modifier = Modifier.weight(1f))
                        TextButton(onClick = onCancel) { Text(stringResource(R.string.abort)) }
                        TextButton(onClick = onConfirm) { Text(stringResource(R.string.ok)) }
                    }
                }
            }
        }
    }

    @Composable
    fun ShowDatePicker(
        initialDate: LocalDate,
        today: LocalDate = LocalDate.now(),
        onDateChange: (LocalDate) -> Unit = {}
    ) {
        var showDatePicker by remember { mutableStateOf(false) }
        val state = rememberDatePickerState(
            initialSelectedDateMillis = initialDate.toEpochMillis(),
            yearRange = IntRange(Math.min(today.year, initialDate.year), today.year + 1),
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return utcTimeMillis >= today.toEpochMillis()
                }

                override fun isSelectableYear(year: Int): Boolean {
                    return year >= today.year
                }
            }
        )
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text(
                            stringResource(R.string.abort)
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        state.selectedDateMillis?.let { onDateChange(localDateOfEpochMillis(it)) }
                        showDatePicker = false
                    }) {
                        Text(stringResource(R.string.ok))
                    }
                }
            ) {
                DatePicker(
                    state = state,
                )
            }
        }

        FilterChip(
            selected = false,
            onClick = { showDatePicker = true },
            label = { Text(stringResource(R.string.custom)) }, //TODO show selected date
            modifier = Modifier.padding(end = 8.dp)
        )
    }
}