package dev.rohitrai.assistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import dev.rohitrai.assistant.ui.screen.MainScreen
import dev.rohitrai.assistant.ui.screen.MainScreenViewModel
import dev.rohitrai.assistant.ui.theme.AssistantTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MainScreenViewModel by viewModels { MainScreenViewModel.factory() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AssistantTheme {
                MainScreen(viewModel)
            }
        }
    }
}
