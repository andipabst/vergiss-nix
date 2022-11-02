package de.andicodes.vergissnix.ui.main


import android.text.format.DateFormat
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.flowlayout.FlowRow
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.DatePickerDefaults
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.TimePickerDefaults
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import de.andicodes.vergissnix.R
import de.andicodes.vergissnix.data.TimeHelper
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit
import java.time.temporal.Temporal

@ExperimentalMaterialApi
@ExperimentalMaterial3Api
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
                    Divider(modifier = Modifier.padding(top = 16.dp))

                    SectionHeadline(
                        text = stringResource(R.string.date),
                        icon = painterResource(id = R.drawable.ic_baseline_calendar_today_24)
                    )

                    FlowRow {
                        val originalDate = originalTask?.time?.toLocalDate()
                        TimeHelper.getDateRecommendations(originalDate)
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

                    Divider(modifier = Modifier.padding(top = 16.dp))

                    SectionHeadline(
                        text = stringResource(R.string.time),
                        icon = painterResource(id = R.drawable.ic_baseline_access_time_24)
                    )

                    FlowRow {
                        val originalTime = originalTask?.time?.toLocalTime()
                        TimeHelper.getTimeRecommendations(originalTime)
                            .forEach { timeRecommendation ->
                                TimeRecommendationChip(
                                    time = timeRecommendation,
                                    currentlySelectedTime = selectedTime,
                                    onSelected = { viewModel.setTime(it) }
                                )
                            }

                        ShowTimePicker(
                            initialTime = originalTime ?: LocalTime.now().plusHours(1)
                                .withMinute(0),
                            use24hour = DateFormat.is24HourFormat(context)
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
        var label = date.format(DateTimeFormatter.ofPattern("dd.MM."))
        if (date == today) {
            label += " (Heute)"
        } else if (date == today.plusDays(1)) {
            label += " (Morgen)"
        } else {
            label += " (" + date.format(DateTimeFormatter.ofPattern("E")) + ")"
        }

        RecommendationChip(
            label = label,
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
        Box(modifier = Modifier.padding(top = 8.dp)) {
            BasicTextField(
                value = text,
                onValueChange = onTextChange,
                textStyle = MaterialTheme.typography.titleLarge,
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
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.outline
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
        val dialogState = rememberMaterialDialogState()
        MaterialDialog(
            dialogState = dialogState,
            buttons = {
                positiveButton(stringResource(R.string.ok))
                negativeButton(stringResource(R.string.abort))
            }
        ) {
            timepicker(
                initialTime = initialTime,
                is24HourClock = use24hour,
                title = stringResource(R.string.chooseTime),
                colors = TimePickerDefaults.colors(
                    activeBackgroundColor = MaterialTheme.colorScheme.primary,
                    inactiveBackgroundColor = MaterialTheme.colorScheme.outline.copy(0.3f),
                    activeTextColor = MaterialTheme.colorScheme.onPrimary,
                    inactiveTextColor = MaterialTheme.colorScheme.onBackground,
                    inactivePeriodBackground = Color.Transparent,
                    selectorColor = MaterialTheme.colorScheme.primary,
                    selectorTextColor = MaterialTheme.colorScheme.onPrimary,
                    headerTextColor = MaterialTheme.colorScheme.onBackground,
                    borderColor = MaterialTheme.colorScheme.onBackground
                ),
            ) { time ->
                onTimeChange(time)
            }
        }

        FilterChip(
            selected = false,
            onClick = { dialogState.show() },
            label = { Text("Benutzerdef.") },
            modifier = Modifier.padding(end = 8.dp)
        )
    }

    @Composable
    fun ShowDatePicker(
        initialDate: LocalDate,
        today: LocalDate = LocalDate.now(),
        onDateChange: (LocalDate) -> Unit = {}
    ) {
        val dialogState = rememberMaterialDialogState()
        MaterialDialog(
            dialogState = dialogState,
            buttons = {
                positiveButton(stringResource(R.string.ok))
                negativeButton(stringResource(R.string.abort))
            }
        ) {
            datepicker(
                initialDate = initialDate,
                title = stringResource(R.string.chooseDate),
                yearRange = IntRange(today.year, 2100),
                allowedDateValidator = { !it.isBefore(today) },
                colors = DatePickerDefaults.colors(
                    headerBackgroundColor = MaterialTheme.colorScheme.primaryContainer,
                    headerTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    calendarHeaderTextColor = MaterialTheme.colorScheme.onBackground,
                    dateActiveBackgroundColor = MaterialTheme.colorScheme.primary,
                    dateInactiveBackgroundColor = Color.Transparent,
                    dateActiveTextColor = MaterialTheme.colorScheme.onPrimary,
                    dateInactiveTextColor = MaterialTheme.colorScheme.onBackground
                ),
            ) { time ->
                onDateChange(time)
            }
        }

        FilterChip(
            selected = false,
            onClick = { dialogState.show() },
            label = { Text("Benutzerdef.") },
            modifier = Modifier.padding(end = 8.dp)
        )
    }

}