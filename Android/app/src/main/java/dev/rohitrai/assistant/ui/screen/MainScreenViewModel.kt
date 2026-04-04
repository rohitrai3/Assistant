package dev.rohitrai.assistant.ui.screen

import android.app.Application
import android.content.Context
import android.content.res.AssetManager
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.Executors

class MainScreenViewModel(private val application: Application) : ViewModel() {
//    var pingResponse by mutableStateOf("")
//        private set
    val status = mutableStateOf("")
//    val messages = mutableStateListOf<Message>()
//    private var whisperContext: WhisperContext? = null
    private var ptr: Long = 0
    private val scope: CoroutineScope = CoroutineScope(
        Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    )
    private val samplesPath = File(application.filesDir, "samples")

    init {
//        ping()
        System.loadLibrary("Assistant")
        viewModelScope.launch {
            printSystemInfo()
            copyAssets()
            loadBaseModel()
//            transcribeSample()
        }
    }

    external fun startRecordingStream(): Int
    external fun stopRecordingStream(): FloatArray
    external fun startPlaybackStream(): Int
    external fun stopPlaybackStream(): Int
    external fun initContextFromAsset(assetManager: AssetManager, assetPath: String): Long
    external fun fullTranscribe(contextPtr: Long, numThreads: Int, audioData: FloatArray)
    external fun getTextSegmentCount(contextPtr: Long): Int
    external fun getTextSegment(contextPtr: Long, index: Int): String
    external fun getSystemInfo(): String
    external fun stopRecordingAndTranscribe(contextPtr: Long, numThreads: Int)

    fun transcribeSample() = viewModelScope.launch {
        transcribeSampleAudio(getFirstSample())
    }

    private suspend fun transcribeSampleAudio(file: File) {
        status.value = "Reading wave samples... "
        val data = readAudioSamples(file)
        status.value = "${data.size / (16000 / 1000)} ms\n"
        status.value = "Transcribing data...\n"
        val start = System.currentTimeMillis()
        val text = transcribeData(data)
        val elapsed = System.currentTimeMillis() - start
        status.value = "Done ($elapsed ms): \n$text\n"
    }

    private suspend fun transcribeAudio(audioData: FloatArray) {
        val text = transcribeData(audioData)
        status.value = "Transcribed text: $text"
    }

    private suspend fun stopAndTranscribeAudio() {
        val text = stopRecordingAndTranscribeData()
        status.value = "Transcribed text: $text"
    }

    private suspend fun readAudioSamples(file: File): FloatArray = withContext(Dispatchers.IO) {
        return@withContext decodeWaveFile(file)
    }

    private suspend fun getFirstSample(): File = withContext(Dispatchers.IO) {
        samplesPath.listFiles()!!.first()
    }

    suspend fun transcribeData(data: FloatArray): String = withContext(scope.coroutineContext) {
        status.value = "Transcribing data..."
        require(ptr != 0L)
        val numThreads = WhisperCpuConfig.preferredThreadCount
        Log.d("transcribeData()", "Selecting $numThreads threads")
        fullTranscribe(ptr, numThreads, data)
        val textCount = getTextSegmentCount(ptr)
        Log.i("transcribeData()", "Text count: $textCount")
        return@withContext buildString {
            for (i in 0 until textCount) {
                append(getTextSegment(ptr, i))
            }
        }
    }

    suspend fun stopRecordingAndTranscribeData(): String = withContext(scope.coroutineContext) {
        status.value = "Transcribing data..."
        require(ptr != 0L)
        val numThreads = WhisperCpuConfig.preferredThreadCount
        Log.d("transcribeData()", "Selecting $numThreads threads")
        stopRecordingAndTranscribe(ptr, numThreads)
        val textCount = getTextSegmentCount(ptr)
        Log.i("transcribeData()", "Text count: $textCount")
        return@withContext buildString {
            for (i in 0 until textCount) {
                append(getTextSegment(ptr, i))
            }
        }
    }

    private suspend fun printSystemInfo() {
        status.value = String.format("System Info: %s\n", getSystemInfo())
    }

    fun createContextFromAsset(assetManager: AssetManager, assetPath: String): Long {
        val ptr = initContextFromAsset(assetManager, assetPath)

        if (ptr == 0L) {
            throw RuntimeException("Couldn't create from asset $assetPath")
        }

        return ptr
    }

    private suspend fun copyAssets() = withContext(Dispatchers.IO) {
        samplesPath.mkdirs()
        application.copyData("samples", samplesPath)
        status.value = "All data copied to working directory"
    }

    private suspend fun Context.copyData(assetDirName: String, destDir: File) = withContext(
        Dispatchers.IO) {
        assets.list(assetDirName)?.forEach { name ->
            val assetPath = "$assetDirName/$name"
            status.value = "Processing $assetPath"
            val destination = File(destDir, name)
            status.value = "Copying $assetPath to $destination"
            assets.open(assetPath).use { input ->
                destination.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            status.value = "Copied $assetPath to $destination"
        }
    }

    private suspend fun loadBaseModel() = withContext(Dispatchers.IO) {
//        Log.i("loadBaseModel()", "Loading model...")
        status.value = "Loading model..."
        val models = application.assets.list("models/")
        if (models != null) {
            ptr = createContextFromAsset(application.assets, "models/" + models[0])
            status.value = "Loaded model ${models[0]}"
//            Log.i("loadBaseModel()", "Loaded model ${models[0]}")
        }
    }

    fun toggleRecord(isMicOn: Boolean) = viewModelScope.launch {
        if (isMicOn) {
            val result = startRecordingStream()
            status.value = "Start recording stream response: $result"
        } else {
//            val audioData = stopRecordingStream()
//            status.value = "Recording data size: ${audioData.size}"
////            audioData.forEach { Log.i("Recording data: ", it.toString()) }
////            transcribeAudio(audioData)
//            status.value = "Playing recording..."
////            startPlaybackStream()
//            transcribeAudio(audioData)
            stopAndTranscribeAudio()
        }
//        try {
//            if (isMicOn) {
////                recorder.stopRecording()
////                isRecording = false
//            }
//        }
    }

//    fun ping() {
//        viewModelScope.launch {
//            try {
//                status.value = "Pinging..."
//                val response = AssistantApi.retrofitService.ping()
//                pingResponse = response.status
//                status.value = "Ping complete."
//            } catch (e: Exception) {
//                Log.e(
//                    "MainScreenViewModel",
//                    "Exception occurred while calling ping: ",
//                    e
//                )
//                status.value = "Error calling Ping."
//            }
//        }
//    }

//    fun send(message: String) {
//        viewModelScope.launch {
//            try {
//                status.value = "Sending..."
//                messages.add(Message(message, "user"))
//                val response = withContext(Dispatchers.IO) {
//                    AssistantApi.retrofitService.send(Request(messages, true))
//                }
//                status.value = "Sent."
//                status.value = "Thinking..."
//                val assistantMessage = Message("", "assistant")
//                messages.add(assistantMessage)
//                response.charStream().forEachLine { line ->
//                    if (line.startsWith("data: ")) {
//                        if (line != "data: [DONE]") {
//                            val data = Json.decodeFromString<Response>(
//                                line.substring(5)
//                            )
//                            if (data.choices[0].delta.content != null) {
//                                assistantMessage.content += data.choices[0].delta.content
//                            }
//                        }
//                    }
//                 }
//                status.value = "Received."
//            } catch (e: Exception) {
//                Log.e(
//                    "MainScreenViewModel",
//                    "Exception occurred while calling send: ",
//                    e
//                )
//                status.value = "Error calling send."
//            }
//        }
//    }

    companion object {
        fun factory() = viewModelFactory {
            initializer {
                val application =
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                MainScreenViewModel(application)
            }
        }
    }
}