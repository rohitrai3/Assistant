package dev.rohitrai.assistant.ui.screen

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.rohitrai.assistant.data.Message
import dev.rohitrai.assistant.data.Request
import dev.rohitrai.assistant.network.AssistantApi
import kotlinx.coroutines.launch

class MainScreenViewModel : ViewModel() {
    var pingResponse by mutableStateOf("")
        private set
    val status = mutableStateOf("")
    val messages = mutableListOf<Message>()

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
                Log.e("MainScreenViewModel", "Exception occurred while calling ping: ", e)
                status.value = "Error calling Ping."
            }
        }
    }

    fun send(message: String) {
        viewModelScope.launch {
            try {
                status.value = "Sending..."
                messages.add(Message(message, "user"))
                val request = Request(messages)
                val reply = AssistantApi.retrofitService.send(request)
                status.value = "Send complete."
                status.value = "Reply: ${reply.choices[0].message.content}"
            } catch (e: Exception) {
                Log.e("MainScreenViewModel", "Exception occurred while calling send: ", e)
                status.value = "Error calling send."
            }
        }
    }

    companion object {
        fun factory() = viewModelFactory {
            initializer {
                MainScreenViewModel()
            }
        }
    }
}