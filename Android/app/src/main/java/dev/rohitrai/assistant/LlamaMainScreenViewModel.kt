package dev.rohitrai.assistant

import android.R.attr.name
import android.R.id.input
import android.app.Application
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.net.Uri
import android.os.SharedMemory
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.KeyboardType.Companion.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

class LlamaMainScreenViewModel(private val application: Application) : ViewModel() {
    private lateinit var engine: InferenceEngine
    private var generationJob: Job? = null
    private val lastAssistantMsg = StringBuilder()
    val messages = mutableListOf(Message("1", "List of messages", false))
    private val messageAdapter = MessageAdapter(messages)
    private val modelsPath = File(application.filesDir, "models/llm")
    private val isModelReady = mutableStateOf(false)
    val userInputText = mutableStateOf("")
    val isUserInputEnabled = mutableStateOf(false)
    val userInputHint = mutableStateOf("Application started")
    val ggufTv = mutableStateOf("Application started")
    val isModelsListExpanded = mutableStateOf(false)
    var models: Array<String>? = application.assets.list("models/llm")
    val selectedModel = mutableStateOf("None")


    init {
        viewModelScope.launch(Dispatchers.Default) {
            copyModels()
            engine = AiChat.getInferenceEngine(application.applicationContext)
        }
    }

    fun handleSelectedModel() {
        val uri = File(application.filesDir, "models/llm/${selectedModel.value}").toUri()
        userInputHint.value = "Parsing GGUF..."
        ggufTv.value = "Parsing metadata from selected file \n$uri"

        viewModelScope.launch(Dispatchers.IO) {
            Log.i(TAG, "Parsing GGUF metadata...")
            application.contentResolver.openInputStream(uri)?.use {
                GgufMetadataReader.create().readStructuredMetadata(it)
            }?.let { metadata ->
                Log.i(TAG, "GGUF parsed: \n$metadata")
                withContext(Dispatchers.Main) {
                    ggufTv.value = metadata.toString()
                }

                val modelName = metadata.filename() + FILE_EXTENSION_GGUF
                application.contentResolver.openInputStream(uri)?.use { input ->
                    ensureModelFile(modelName, input)
                }?.let { modelFile ->
                    loadModel(modelName, modelFile)

                    withContext(Dispatchers.Main) {
                        isModelReady.value = true
                        userInputHint.value = "Type and send a message!"
                        isUserInputEnabled.value = true
                    }
                }
            }
        }
    }

    private suspend fun copyModels() = withContext(Dispatchers.IO) {
        Log.i(TAG, "Copying models to working directory.\n")
        modelsPath.mkdirs()
        application.copyData("models/llm", modelsPath)
        Log.i(TAG, "All data copied to working directory.\n")
    }

    private suspend fun ensureModelFile(modelName: String, input: InputStream) =
        withContext(Dispatchers.IO) {
            File(ensureModelsDirectory(), modelName).also { file ->
                if (!file.exists()) {
                    Log.i(TAG, "Start copying file to $modelName")
                    withContext(Dispatchers.Main) {
                        userInputHint.value = "Copying file..."
                    }

                    FileOutputStream(file).use { input.copyTo(it) }
                    Log.i(TAG, "Finished copying file to $modelName")
                } else {
                    Log.i(TAG, "File already exists $modelName")
                }
            }
        }

    private fun ensureModelsDirectory() =
        File(modelsPath, DIRECTORY_MODELS).also {
            if (it.exists() && !it.isDirectory) { it.delete() }
            if (!it.exists()) { it.mkdir() }
        }

    suspend fun loadModel(modelName: String, modelFile: File) =
        withContext(Dispatchers.IO) {
            Log.i(TAG, "Loading model $modelName")
            withContext(Dispatchers.Main) {
                userInputHint.value = "Loading model..."
            }
            engine.loadModel(modelFile.path)
        }

    fun handleUserInput() {
        userInputText.value.also { userMsg ->
            if (userMsg.isEmpty()) {
                Toast.makeText(application.applicationContext, "Input message is empty!", Toast.LENGTH_SHORT).show()
            } else {
                userInputText.value = ""
                isUserInputEnabled.value = false

                messages.add(Message(UUID.randomUUID().toString(), userMsg, true))
                lastAssistantMsg.clear()
                messages.add(Message(UUID.randomUUID().toString(), lastAssistantMsg.toString(), false))

                generationJob = viewModelScope.launch(Dispatchers.Default) {
                    engine.sendUserPrompt(userMsg)
                        .onCompletion {
                            withContext(Dispatchers.Main) {
                                isUserInputEnabled.value = true
                            }
                        }.collect { token ->
                            withContext(Dispatchers.Main) {
                                val messageCount = messages.size
                                check(messageCount > 0 && !messages[messageCount - 1].isUser)

                                messages.removeAt(messageCount - 1).copy(
                                    content = lastAssistantMsg.append(token).toString()
                                ).let { messages.add(it) }

//                                messageAdapter.notifyItemChanged(messages.size - 1)
                            }
                        }
                }
            }
        }
    }

    companion object {
        private val TAG = LlamaMainScreenViewModel::class.java.simpleName

        private const val DIRECTORY_MODELS = "models"
        private const val FILE_EXTENSION_GGUF = ".gguf"
        fun factory() = viewModelFactory {
            initializer {
                val application =
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                LlamaMainScreenViewModel(application)
            }
        }
    }

    fun GgufMetadata.filename() = when {
        basic.name != null -> {
            basic.name?.let { name ->
                basic.sizeLabel?.let { size ->
                    "$name-$size"
                } ?: name
            }
        }
        architecture?.architecture != null -> {
            architecture?.architecture?.let { arch ->
                basic.uuid?.let { uuid ->
                    "$arch-$uuid"
                } ?: "$arch-${System.currentTimeMillis()}"
            }
        }
        else -> {
            "model -${System.currentTimeMillis().toHexString()}"
        }
    }

    @Composable
    fun MainScreen() {
        Button(onClick = { handleUserInput() }) { }
    }
}

private suspend fun Context.copyData(
    assetDirName: String,
    destDir: File
) = withContext(Dispatchers.IO) {
    val TAG = LlamaMainScreenViewModel::class.java.simpleName

    assets.list(assetDirName)?.forEach { name ->
        val assetPath = "$assetDirName/$name"
        Log.v(TAG, "Processing $assetPath...")
        val destination = File(destDir, name)
        Log.v(TAG, "Copying $assetPath to $destination...")
        Log.i(TAG, "Copying $name...\n")
        assets.open(assetPath).use { input ->
            destination.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        Log.v(TAG, "L206: Copied $assetPath to $destination")
    }
}
