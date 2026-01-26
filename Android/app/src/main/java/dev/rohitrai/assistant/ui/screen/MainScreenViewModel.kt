package dev.rohitrai.assistant.ui.screen

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.rohitrai.assistant.data.Message
import dev.rohitrai.assistant.data.Request
import dev.rohitrai.assistant.data.Response
import dev.rohitrai.assistant.network.AssistantApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class MainScreenViewModel(application: Application) : ViewModel() {
    var pingResponse by mutableStateOf("")
        private set
    val status = mutableStateOf("")
    val messages = mutableStateListOf<Message>()

    init {
        ping()
    }

    fun ping() {
        viewModelScope.launch {
            try {
                status.value = "Pinging..."
                val response = AssistantApi.retrofitService.ping()
                pingResponse = response.status
                status.value = "Ping complete."
            } catch (e: Exception) {
                Log.e(
                    "MainScreenViewModel",
                    "Exception occurred while calling ping: ",
                    e
                )
                status.value = "Error calling Ping."
            }
        }
    }

    fun send(message: String) {
        viewModelScope.launch {
            try {
                status.value = "Sending..."
                messages.add(Message(message, "user"))
                val response = withContext(Dispatchers.IO) {
                    AssistantApi.retrofitService.send(Request(messages, true))
                }
                status.value = "Sent."
                status.value = "Thinking..."
                val assistantMessage = Message("", "assistant")
                messages.add(assistantMessage)
                response.charStream().forEachLine { line ->
                    if (line.startsWith("data: ")) {
                        if (line != "data: [DONE]") {
                            val data = Json.decodeFromString<Response>(
                                line.substring(5)
                            )
                            if (data.choices[0].delta.content != null) {
                                assistantMessage.content += data.choices[0].delta.content
                            }
                        }
                    }
                 }
                status.value = "Received."
            } catch (e: Exception) {
                Log.e(
                    "MainScreenViewModel",
                    "Exception occurred while calling send: ",
                    e
                )
                status.value = "Error calling send."
            }
        }
    }

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