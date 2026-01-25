package dev.rohitrai.assistant.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Scaffold
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

private val TAG = "MainScreen"

@Composable
fun MainScreen() {
    val promptInputErrorMessage = stringResource(R.string.prompt_input_error_message)
    val isError = remember { mutableStateOf(false) }
    val promptInputLabel = stringResource(R.string.prompt_input_label)
    val promptInputState = rememberTextFieldState()

    fun sendPrompt() {
        Log.i(TAG, "Sending prompt...")
    }

    ScreenContent(
        isError = isError,
        sendPrompt = { sendPrompt() },
        promptInputErrorMessage = promptInputErrorMessage,
        promptInputLabel = promptInputLabel,
        promptInputState = promptInputState
    )
}

@Composable
private fun ScreenContent(
    isError: MutableState<Boolean>,
    sendPrompt: () -> Unit,
    promptInputLabel: String,
    promptInputErrorMessage: String,
    promptInputState: TextFieldState
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextInput(
                    errorMessage = promptInputErrorMessage,
                    isError = isError,
                    label = promptInputLabel,
                    modifier = Modifier.weight(1f),
                    state = promptInputState
                )
                SendButton(sendPrompt)
            }
        }
    }
}
