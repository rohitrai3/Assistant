package dev.rohitrai.assistant.ui.screen

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioManager
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import dev.rohitrai.assistant.ui.component.MicButton

@Composable
@SuppressLint("MissingPermission")
fun MainScreen(viewModel: MainScreenViewModel, context: Context) {
//    val promptInputErrorMessage = stringResource(R.string.prompt_input_error_message)
//    val isError = remember { mutableStateOf(false) }
//    val promptInputLabel = stringResource(R.string.prompt_input_label)
//    val promptInputState = rememberTextFieldState()
    val isMicOn = remember {mutableStateOf(false)}
//
//    viewModel.status.value = stringResource(R.string.status_application_started)
//
//    fun sendPrompt() {
//        viewModel.send(promptInputState.text.toString())
//        promptInputState.clearText()
//    }

    fun micOffAction() {
//        viewModel.stopAudioStreamNative()
        isMicOn.value = false
        viewModel.status.value = "Recorded."
        viewModel.toggleRecord(isMicOn.value)
    }

    fun micOnAction() {
        isMicOn.value = true
//        viewModel.startAudioStreamNative()
        viewModel.status.value = "Recording..."
        viewModel.toggleRecord(isMicOn.value)
    }

    ScreenContent(
//        isError = isError,
        isMicOn = isMicOn,
//        messages = viewModel.messages,
        micOffAction = { micOffAction() },
        micOnAction = { micOnAction() },
//        promptInputErrorMessage = promptInputErrorMessage,
//        promptInputLabel = promptInputLabel,
//        promptInputState = promptInputState,
//        sendPrompt = { sendPrompt() },
        status = viewModel.status,
        context = context
    )
}

@Composable
private fun ScreenContent(
//    isError: MutableState<Boolean>,
    isMicOn: MutableState<Boolean>,
//    messages: List<Message>,
    micOffAction: () -> Unit,
    micOnAction: () -> Unit,
//    promptInputLabel: String,
//    promptInputErrorMessage: String,
//    promptInputState: TextFieldState,
//    sendPrompt: () -> Unit,
    status: MutableState<String>,
    context: Context
) {
    val OBOE_API_AAUDIO = 0;
    val OBOE_API_OPENSL_ES = 1;
    val isPlaying = remember { mutableStateOf(false) }
    val toggleButtonText = remember { mutableStateOf("Start Effect") }
    val liveEffectEngine = LiveEffectEngine()
    val isSlesButtonEnable = remember { mutableStateOf(false) }
    val isAAudioButtonEnable = remember { mutableStateOf(false) }
    val apiSelection = remember { mutableStateOf(OBOE_API_AAUDIO) }
    val mAAudioRecommended = remember { mutableStateOf(true) }
    val recordingDeviceId = remember { mutableStateOf(15) }
//    val playbackDeviceId = remember { mutableStateOf(151) }
//    val recordingDeviceType = AudioManager.GET_DEVICES_INPUTS
//    val playbackDeviceType = AudioManager.GET_DEVICES_OUTPUTS
    val mAudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
//    val recordingDevices = mAudioManager.getDevices(recordingDeviceType)
//    val playbackDevices = mAudioManager.getDevices(playbackDeviceType)

    fun resetStatusView() {
        status.value = "Warning"
    }

    fun EnableAudioApiUI(enable: Boolean) {
        if (apiSelection.value == OBOE_API_AAUDIO && !mAAudioRecommended.value) {
            apiSelection.value = OBOE_API_OPENSL_ES;
        }
        isSlesButtonEnable.value = enable
        if (!mAAudioRecommended.value) {
            isAAudioButtonEnable.value = false
        } else {
            isAAudioButtonEnable.value = enable
        }
    }

    fun stopEffect() {
        Log.d("stopEffect", "Playing, attempting to stop.")
        liveEffectEngine.setEffectOn(false)
        resetStatusView();
        toggleButtonText.value = "Start Effect"
        isPlaying.value = false
        EnableAudioApiUI(true)
    }

    fun startEffect() {
        Log.d("startEffect()", "Attempting to start.")
        val success = liveEffectEngine.setEffectOn(true)
        if (success) {
            status.value = "Playing"
            toggleButtonText.value = "Stop Effect"
            isPlaying.value = true
            EnableAudioApiUI(false)
        } else {
            status.value = "Open failed"
            isPlaying.value = false
        }
    }

    fun toggleEffect() {
        if (isPlaying.value) {
            stopEffect()
        } else {
            liveEffectEngine.setAPI(apiSelection.value)
            startEffect()
        }
    }

//    fun getRecordingDeviceId(): Int {
//        return recordingDeviceId.value
//    }
//
//    fun getPlaybackDeviceId(): Int {
//        return playbackDeviceId.value
//    }
//
//    fun startForegroundService() {
//        val serviceIntent = Intent(ACTION_START, null, context,
//            DuplexStreamForegroundService.Companion::class.java
//        )
//        startForegroundService(context, serviceIntent)
//    }

    fun onStartTest() {
        liveEffectEngine.create()
        mAAudioRecommended.value = liveEffectEngine.isAAudioRecommended()
        EnableAudioApiUI(true)
        liveEffectEngine.setAPI(apiSelection.value)
    }

//    liveEffectEngine.setRecordingDeviceId(
//        getRecordingDeviceId()
//    )
//
//    liveEffectEngine.setRecordingDeviceId(
//        getPlaybackDeviceId()
//    )

    liveEffectEngine.setDefaultStreamValues(context);
    liveEffectEngine.setRecordingDeviceId(recordingDeviceId.value)

//    startForegroundService()

    onStartTest();

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
//            MessageBox(messages, Modifier.weight(1f))
//            Row(
//                horizontalArrangement = Arrangement.spacedBy(8.dp),
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .imePadding()
//            ) {
//                TextInput(
//                    errorMessage = promptInputErrorMessage,
//                    isError = isError,
//                    label = promptInputLabel,
//                    modifier = Modifier.weight(1f),
//                    state = promptInputState
//                )
                MicButton(isMicOn.value, micOffAction, micOnAction)
//                if (promptInputState.text.isNotEmpty()) SendButton(sendPrompt)
//            }
//            Column {
//                Text("${stringResource(R.string.status_label)}: ${status.value}")
//                Text("Recording device type: $recordingDeviceType")
//                Text("Selected recording device: ${recordingDeviceId.value}")
//                Text("Playback device type: $playbackDeviceType")
//                Text("Selected playback device: ${playbackDeviceId.value}")
//                Text("Recording devices:")
////                recordingDevices.forEach { device ->
////                    Text(
////                        "Device Id: ${device.id}, Device name: ${
////                            AudioDeviceInfoConverter.typeToString(
////                                device.type
////                            )
////                        }"
////                    )
////                }
//                Text("Playback devices:")
//                playbackDevices.forEach { device ->
//                    Text(
//                        "Device Id: ${device.id}, Device name: ${
//                            AudioDeviceInfoConverter.typeToString(
//                                device.type
//                            )
//                        }"
//                    )
//                }
                Button({ toggleEffect() }) { Text(toggleButtonText.value) }
////                recordingDevices.forEach { device ->
////                    Button({
////                        recordingDeviceId.value = device.id
////                        liveEffectEngine.setRecordingDeviceId(
////                            getRecordingDeviceId()
////                        )
////                    }) { Text("Recording device ${AudioDeviceInfoConverter.typeToString(device.type)}") }
////                }
////                playbackDevices.forEach { device ->
////                    Button({
////                        playbackDeviceId.value = device.id
////                        liveEffectEngine.setRecordingDeviceId(
////                            getPlaybackDeviceId()
////                        )
////                    }) { Text("Recording device ${AudioDeviceInfoConverter.typeToString(device.type)}") }
////                }
//                Button(
//                    { apiSelection.value = OBOE_API_AAUDIO },
//                    enabled = isAAudioButtonEnable.value
//                ) { Text("AAudio") }
//                Button(
//                    { apiSelection.value = OBOE_API_OPENSL_ES },
//                    enabled = isSlesButtonEnable.value
//                ) { Text("SLES") }
//            }
        }
    }
}
