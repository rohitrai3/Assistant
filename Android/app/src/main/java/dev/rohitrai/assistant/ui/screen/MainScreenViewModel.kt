package dev.rohitrai.assistant.ui.screen

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.rohitrai.assistant.network.AssistantApi
import kotlinx.coroutines.launch

class MainScreenViewModel : ViewModel() {
    var pingResponse by mutableStateOf("")
        private set
    val status = mutableStateOf("")

    init {
        ping()
    }

    fun ping() {
        viewModelScope.launch {
            try {
                val response = AssistantApi.retrofitService.ping()
                pingResponse = response.status
            } catch (e: Exception) {
                Log.e("MainScreenViewModel", "Exception occurred while calling ping: ", e)
                status.value ="Error calling Ping"
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