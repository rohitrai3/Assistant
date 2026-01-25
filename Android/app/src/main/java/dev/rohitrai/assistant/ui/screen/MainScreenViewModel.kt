package dev.rohitrai.assistant.ui.screen

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.rohitrai.assistant.network.AssistantApi
import kotlinx.coroutines.launch

class MainScreenViewModel(private val application: Application) : ViewModel() {
    var pingResponse by mutableStateOf("")
        private set
    val status = mutableStateOf("")

    fun ping() {
        viewModelScope.launch {
            val response = AssistantApi.retrofitService.ping()
            pingResponse = response
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