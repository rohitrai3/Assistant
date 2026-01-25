package dev.rohitrai.assistant

import android.R.attr.onClick
import android.R.attr.value
import android.R.id.message
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.text.TextStyle
import com.rohitrai.finance.ui.theme.White

@Composable
fun LlamaMainScreen(viewModel: LlamaMainScreenViewModel) {
    LlamaMainScreen(
        handleUserInput = viewModel::handleUserInput,
        userInputText = viewModel.userInputText,
        userInputHint = viewModel.userInputHint,
        isModelsListExpanded = viewModel.isModelsListExpanded,
        models = viewModel.models,
        selectedModel = viewModel.selectedModel,
        handleSelectedModel = viewModel::handleSelectedModel,
        messages = viewModel.messages,
        isUserInputEnabled = viewModel.isUserInputEnabled
    )
}

@Composable
private fun LlamaMainScreen(
    handleUserInput: () -> Unit,
    userInputText: MutableState<String>,
    userInputHint: MutableState<String>,
    isModelsListExpanded: MutableState<Boolean>,
    models: Array<String>?,
    selectedModel: MutableState<String>,
    handleSelectedModel: () -> Unit,
    messages: MutableList<Message>,
    isUserInputEnabled: MutableState<Boolean>
) {
    Column {
        Text(
            text = "Selected model: ${selectedModel.value}",
            color = White
        )
        Button(onClick = { isModelsListExpanded.value = true }) { Text("Select model") }
        DropdownMenu(expanded = isModelsListExpanded.value, onDismissRequest = { isModelsListExpanded.value = false }) {
            DropdownMenuItem(
                text = { Text("None") },
                onClick = {
                    selectedModel.value = "None"
                    isModelsListExpanded.value = false
                }
            )
            models?.forEach {
                DropdownMenuItem(
                    text = { Text(it) },
                    onClick = {
                        selectedModel.value = it
                        isModelsListExpanded.value = false
                    }
                )
            }
        }
        Button(onClick = handleSelectedModel) { Text("Load model") }
        OutlinedTextField(
            value = userInputText.value,
            onValueChange = { userInputText.value = it },
            placeholder = { Text(userInputHint.value) },
            textStyle = TextStyle(color = White)
        )
        Button(onClick = handleUserInput, enabled = isUserInputEnabled.value) {
            Text("Send")
        }
        messages.forEach { message ->
            Text(
                text = message.content,
                color = White
            )
        }
    }
}
