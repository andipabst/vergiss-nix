package de.andicodes.vergissnix.ui.main

import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.widget.EditText
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
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
            content = {
                Column {
                    val text by viewModel.text.observeAsState()
                    OutlinedTextField(
                        value = text ?: "",
                        onValueChange = { viewModel.text.value = it },
                        label = { stringResource(R.string.task_name) },
                        placeholder = { stringResource(R.string.task_name) },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Send,
                            keyboardType = KeyboardType.Text
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    /* Date */
                    Text(text = stringResource(R.string.chooseDate))
                    ChipGroup(
                        selectedRecommendation = viewModel.getRecommendationDatetime()
                            .observeAsState().value,
                        selectedCustom = viewModel.getCustomDatetime().observeAsState().value,
                        selectionRecommendationChangedListener = {
                            viewModel.setRecommendationDatetime(
                                it
                            )
                        },
                        selectionCustomChangedListener = { viewModel.setCustomDatetime(it) }
                    )
                    /* Time */
                    Text(text = stringResource(R.string.chooseTime))
                    ChipGroup(
                        selectedRecommendation = viewModel.getRecommendationDatetime()
                            .observeAsState().value,
                        selectedCustom = viewModel.getCustomDatetime().observeAsState().value,
                        selectionRecommendationChangedListener = {
                            viewModel.setRecommendationDatetime(
                                it
                            )
                        },
                        selectionCustomChangedListener = { viewModel.setCustomDatetime(it) }
                    )
                }
            }
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun ChipGroup(
        selectedRecommendation: LocalDateTime? = null,
        selectionRecommendationChangedListener: (LocalDateTime) -> Unit = {},
        selectedCustom: LocalDateTime? = null,
        selectionCustomChangedListener: (LocalDateTime) -> Unit = {},
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                TimeRecommendationChips(context).apply {
                    this.timeRecommendations = getTimeRecommendations(LocalDateTime.now())
                    this.selectionRecommendationChangedListener =
                        selectionRecommendationChangedListener
                    this.selectedRecommendation = selectedRecommendation
                    this.selectionCustomChangedListener = selectionCustomChangedListener
                    this.selectedCustom = selectedCustom
                }
            },
            update = { view ->
                view.selectedRecommendation = selectedRecommendation
                view.selectedCustom = selectedCustom
            }
        )
    }

    // TODO
    private class TimeFormatter(private val editText: EditText) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            val content = s.toString().replace("Test", "<font color='red'>Test</font>")
            val spanned = Html.fromHtml(content, Html.FROM_HTML_MODE_COMPACT)
            if (editText.text.toString() != spanned.toString()) {
                editText.setText(spanned)
            }
        }

        override fun afterTextChanged(s: Editable) {}
    }

    companion object {
        val DEFAULT_TIME: LocalTime = LocalTime.of(9, 0)
    }
}