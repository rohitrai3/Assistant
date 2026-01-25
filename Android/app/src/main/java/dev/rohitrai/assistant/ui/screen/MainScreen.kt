package dev.rohitrai.assistant.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.rohitrai.assistant.R
import dev.rohitrai.assistant.ui.component.SendButton
import dev.rohitrai.assistant.ui.component.TextInput

@Composable
fun MainScreen(viewModel: MainScreenViewModel) {
    val promptInputErrorMessage = stringResource(R.string.prompt_input_error_message)
    val isError = remember { mutableStateOf(false) }
    val promptInputLabel = stringResource(R.string.prompt_input_label)
    val promptInputState = rememberTextFieldState()

    viewModel.status.value = stringResource(R.string.status_application_started)

    fun sendPrompt() {
        viewModel.send(promptInputState.text.toString())
    }

    ScreenContent(
        isError = isError,
        sendPrompt = { sendPrompt() },
        promptInputErrorMessage = promptInputErrorMessage,
        promptInputLabel = promptInputLabel,
        promptInputState = promptInputState,
        status = viewModel.status
    )
}

@Composable
private fun ScreenContent(
    isError: MutableState<Boolean>,
    sendPrompt: () -> Unit,
    promptInputLabel: String,
    promptInputErrorMessage: String,
    promptInputState: TextFieldState,
    status: MutableState<String>
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .consumeWindowInsets(innerPadding),
            verticalArrangement = Arrangement.Bottom
        ) {
            Text("${stringResource(R.string.status_label)}: ${status.value}")
            Spacer(Modifier.weight(1f))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth().imePadding()
            ) {
                TextInput(
                    errorMessage = promptInputErrorMessage,
                    isError = isError,
                    label = promptInputLabel,
                    modifier = Modifier.weight(1f),
                    state = promptInputState
                )
                if (promptInputState.text.isNotEmpty()) SendButton(sendPrompt)
            }
        }
    }
}
